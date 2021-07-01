package me.mat.lite.protocol.connection;

import io.netty.channel.*;
import me.mat.lite.protocol.connection.decoder.LitePacketDecoder;
import me.mat.lite.protocol.connection.decoder.decoders.LitePacketDecoder1_8;
import me.mat.lite.protocol.connection.packet.LitePacket;
import me.mat.lite.protocol.connection.packet.packets.CArmAnimationPacket;
import me.mat.lite.protocol.util.ReflectionUtil;
import me.mat.lite.protocol.util.accessor.accessors.FieldAccessor;
import org.bukkit.Server;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;

public class ConnectionManager implements Listener, PacketHandler {

    private static final FieldAccessor SERVER_HANDLE = new FieldAccessor(
            ReflectionUtil.getBukkitClass("CraftServer"),
            ReflectionUtil.getMinecraftClass("MinecraftServer"),
            0
    );

    private static final FieldAccessor SERVER_CONNECTION = new FieldAccessor(
            ReflectionUtil.getMinecraftClass("MinecraftServer"),
            ReflectionUtil.getMinecraftClass("ServerConnection"),
            0
    );

    private static final FieldAccessor FUTURE_LIST = new FieldAccessor(
            ReflectionUtil.getMinecraftClass("ServerConnection"),
            List.class,
            0
    );

    private static final FieldAccessor DIRECTION = new FieldAccessor(
            ReflectionUtil.getMinecraftClass("PacketDecoder"),
            ReflectionUtil.getMinecraftClass("EnumProtocolDirection"),
            0
    );

    private final List<PlayerConnection> connections;

    // used for injecting the packet decoder
    private ChannelInboundHandlerAdapter serverRegisterHandler;
    private ChannelInitializer<Channel> hackyRegister, channelRegister;

    // used for keeping track of the decoders
    public final Map<Integer, Integer> protocols;
    private LitePacketDecoder<?> liteDecoder;
    private int id;

    public ConnectionManager(Plugin plugin) {
        this.protocols = new HashMap<>();
        this.connections = new ArrayList<>();

        id = 0;

        this.load(plugin);
    }

    @Override
    public void onPacketReceive(Player player, LitePacket packet) {
        if (packet instanceof CArmAnimationPacket) {
            player.sendMessage("You have swung your arm");
        }
    }

    @Override
    public void onPacketSend(Player player, LitePacket packet) {

    }

    private void inject(Channel channel) {
        channel.eventLoop().execute(() -> {
            if (channel.pipeline().get("decoder") != null) {
                Object decoder = channel.pipeline().get("decoder");
                if (decoder != null) {
                    Object direction = DIRECTION.get(decoder);
                    if (direction != null) {
                        if (liteDecoder == null) {
                            liteDecoder = new LitePacketDecoder1_8(channel, this, direction, ++id);
                            channel.pipeline().replace(
                                    "decoder",
                                    "decoder",
                                    liteDecoder
                            );
                        }
                    } else {
                        System.err.println("Invalid direction");
                    }
                } else {
                    System.err.println("Invalid packet decoder");
                }
            } else {
                System.err.println("Decoder was not found");
            }
        });
    }

    private void load(Plugin plugin) {
        Object serverConnection = getServerConnection(plugin.getServer());
        if (serverConnection == null) {
            System.err.println("Server connection was not found");
            return;
        }

        List<ChannelFuture> futures = FUTURE_LIST.get(serverConnection);
        if (futures == null) {
            System.err.println("Failed to obtain the futures list");
            return;
        }

        channelRegister = new ChannelInitializer<Channel>() {

            @Override
            protected void initChannel(Channel channel) {
                try {
                    inject(channel);
                } catch (Exception e) {
                    System.err.println("Error injecting into channel " + channel.toString());
                }
            }

        };

        hackyRegister = new ChannelInitializer<Channel>() {
            @Override
            protected void initChannel(Channel channel) {
                channel.pipeline().addLast(channelRegister);
            }
        };

        serverRegisterHandler = new ChannelInboundHandlerAdapter() {

            @Override
            public void channelRead(ChannelHandlerContext ctx, Object msg) {
                Channel channel = (Channel) msg;
                channel.pipeline().addFirst(hackyRegister);
                ctx.fireChannelRead(msg);
            }
        };

        new BukkitRunnable() {
            @Override
            public void run() {
                futures.forEach(future -> {
                    Channel channel = future.channel();

                    channel.pipeline().addFirst(serverRegisterHandler);

                    System.out.println("Injected server channel " + channel);
                });
            }
        }.runTask(plugin);
    }

    private Object getServerConnection(Server server) {
        return SERVER_CONNECTION.get(getMinecraftServer(server));
    }

    private Object getMinecraftServer(Server server) {
        return SERVER_HANDLE.get(server);
    }

    /**
     * Adds a player connection
     * to the connection manager
     *
     * @param player player that you want to add the connection to
     */

    public void addConnection(Player player) {
        connections.add(new PlayerConnection(
                liteDecoder,
                this,
                player,
                id
        ));
    }

    /**
     * Removes a player connection
     * from the connection manager
     *
     * @param player player that you want to remove the connection from
     */

    public void removeConnection(Player player) {
        // attempt to find the player connection
        Optional<PlayerConnection> optional
                = connections.stream().filter(c -> c.getPlayer().getEntityId() == player.getEntityId()).findFirst();

        // if the player connection was not found
        if (!optional.isPresent()) {

            // return out of the method
            return;
        }

        // get the player connection instance
        PlayerConnection playerConnection = optional.get();

        // close the player connection
        playerConnection.close();

        // and remove the connection form the connection manager
        connections.remove(playerConnection);
    }

    /**
     * Checks if the player already
     * has a connection assigned
     *
     * @param player player that you want to check for
     * @return {@link Boolean}
     */

    public boolean hasConnection(Player player) {
        return connections.stream().anyMatch(c -> c.getPlayer().getEntityId() == player.getEntityId());
    }

    /**
     * Closes the connection manager
     */

    public void close() {
        // close all the connections
        connections.forEach(PlayerConnection::close);

        // and clear the connections list
        connections.clear();
    }

    @EventHandler
    private void onPlayerJoin(PlayerJoinEvent event) {
        // get the player
        Player player = event.getPlayer();

        // if the player already has a connection
        if (hasConnection(player)) {

            // return out of the method
            return;
        }

        // else add the connection
        addConnection(player);
    }

    @EventHandler
    private void onPlayerQuit(PlayerQuitEvent event) {
        // get the player
        Player player = event.getPlayer();

        // if the player does not have a connection
        if (!hasConnection(player)) {

            // return out of the method
            return;
        }

        // else remove the connection
        removeConnection(player);
    }

}
