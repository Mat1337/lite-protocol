package me.mat.lite.protocol.connection;

import io.netty.channel.*;
import me.mat.lite.protocol.connection.decoder.LitePacketDecoder;
import me.mat.lite.protocol.connection.decoder.decoders.LitePacketDecoder1_8;
import me.mat.lite.protocol.connection.encoder.LitePacketEncoder;
import me.mat.lite.protocol.connection.encoder.encoders.LitePacketEncoder1_8;
import me.mat.lite.protocol.connection.listener.ClientHandshakeListener;
import me.mat.lite.protocol.connection.listener.ClientLoginListener;
import me.mat.lite.protocol.connection.listener.ClientPacketListener;
import me.mat.lite.protocol.connection.packet.LitePacket;
import me.mat.lite.protocol.connection.packet.packets.CHandshakePacket;
import me.mat.lite.protocol.util.ProtocolVersion;
import me.mat.lite.protocol.util.ReflectionUtil;
import me.mat.lite.protocol.util.accessor.accessors.FieldAccessor;
import org.bukkit.Server;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.net.SocketAddress;
import java.util.*;

public class ConnectionManager implements Listener, ClientLoginListener, ClientHandshakeListener, ClientPacketListener {

    // name of the vanilla decoder in the pipeline
    public static final String VANILLA_DECODER_KEY = "decoder";

    // name of the vanilla encoder in the pipeline
    public static final String VANILLA_ENCODER_KEY = "encoder";

    // name of the lite decoder in the pipeline
    public static final String LITE_DECODER_KEY = "lite-decoder";

    // name of the lite encoder in the pipeline
    public static final String LITE_ENCODER_KEY = "lite-encoder";

    // used for accessing the MinecraftServer handle
    private static final FieldAccessor SERVER_HANDLE = new FieldAccessor(
            ReflectionUtil.getBukkitClass("CraftServer"),
            ReflectionUtil.getMinecraftClass("MinecraftServer"),
            0
    );

    // used for accessing the ServerConnection handle
    private static final FieldAccessor SERVER_CONNECTION = new FieldAccessor(
            ReflectionUtil.getMinecraftClass("MinecraftServer"),
            ReflectionUtil.getMinecraftClass("ServerConnection"),
            0
    );

    // used for accessing the list of future connections
    private static final FieldAccessor FUTURE_LIST = new FieldAccessor(
            ReflectionUtil.getMinecraftClass("ServerConnection"),
            List.class,
            0
    );

    // used for accessing the direction of the packet decoder
    private static final FieldAccessor DECODER_DIRECTION = new FieldAccessor(
            ReflectionUtil.getMinecraftClass("PacketDecoder"),
            ReflectionUtil.getMinecraftClass("EnumProtocolDirection"),
            0
    );

    // used for accessing the direction of the packet encoder
    private static final FieldAccessor ENCODER_DIRECTION = new FieldAccessor(
            ReflectionUtil.getMinecraftClass("PacketEncoder"),
            ReflectionUtil.getMinecraftClass("EnumProtocolDirection"),
            0
    );

    // used for storing the player connections
    private final List<PlayerConnection> connections;

    // used for injecting the packet decoder
    private ChannelInboundHandlerAdapter serverRegisterHandler;
    private ChannelInitializer<Channel> hackyRegister, channelRegister;

    // used for storing raw protocols
    private final Map<String, Integer> rawProtocols;

    // used for storing player protocols
    private final Map<Player, ProtocolVersion> protocolVersions;

    // used for caching the lite decoder
    private LitePacketDecoder<?> liteDecoder;

    // used for caching the lite encoder
    private LitePacketEncoder<?> liteEncoder;

    public ConnectionManager(Plugin plugin) {
        // define the maps that handle protocol fetching
        this.rawProtocols = new HashMap<>();
        this.protocolVersions = new HashMap<>();

        // define the list that will hold all the connections
        this.connections = new ArrayList<>();

        // load the connection manager
        this.load(plugin);
    }

    @Override
    public boolean onHandshakeReceive(SocketAddress address, LitePacket packet) {
        // if the packet is a handshake packet
        if (packet instanceof CHandshakePacket) {
            // cast the packet to the handshake packet
            CHandshakePacket handshakePacket = (CHandshakePacket) packet;

            // get the pure ip address
            String playerAddress
                    = address.toString().substring(1).split(":")[0];

            // cache the protocol version
            rawProtocols.put(playerAddress, handshakePacket.protocolVersion);
        }
        return false;
    }

    @Override
    public boolean onLoginReceive(SocketAddress address, LitePacket packet) {
        return false;
    }

    @Override
    public boolean onPacketReceive(Player player, LitePacket packet) {
        return false;
    }

    /**
     * Loads the connection manager for the plugin
     *
     * @param plugin plugin that you want to load the connection manager to
     */

    private void load(Plugin plugin) {
        // get the server connection
        Object serverConnection = getServerConnection(plugin.getServer());

        // if the server connection is invalid
        if (serverConnection == null) {
            // log to console
            System.err.println("Server connection was not found");

            // and return out of the method
            return;
        }

        // get the list of future channels
        List<ChannelFuture> futures = FUTURE_LIST.get(serverConnection);

        // if the list is invalid
        if (futures == null) {
            // log to console
            System.err.println("Failed to obtain the futures list");

            // and return out of the method
            return;
        }

        // initialize all the injection handlers
        initializeHandlers();

        // inject the server registry handler
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

    /**
     * Injects the the encoder
     * into the channel
     *
     * @param channel channel that you want to inject in
     */

    private void injectEncoder(Channel channel) {
        // if the lite encoder is valid
        if (liteEncoder != null) {
            // return out of the method
            return;
        }

        // get the channel pipeline
        ChannelPipeline pipeline = channel.pipeline();

        // get the packet decoder from the pipeline
        Object encoder = pipeline.get(VANILLA_ENCODER_KEY);

        // if the encoder is invalid
        if (encoder == null) {
            // return out of the method
            return;
        }

        // get the direction of the encoder
        Object direction = ENCODER_DIRECTION.get(encoder);

        // if the direction of the encoder is invalid
        if (direction == null) {
            // return out of the method
            return;
        }

        // create the decoder
        liteEncoder = new LitePacketEncoder1_8(
                channel,
                direction
        );

        // replace the original decoder with the lite decoder
        pipeline.addAfter(
                VANILLA_ENCODER_KEY,
                LITE_ENCODER_KEY,
                liteEncoder
        );
    }

    /**
     * Injects the the decoder
     * into the channel
     *
     * @param channel channel that you want to inject in
     */

    private void injectDecoder(Channel channel) {
        // if the lite decoder is valid
        if (liteDecoder != null) {
            // return out of the method
            return;
        }

        // get the channel pipeline
        ChannelPipeline pipeline = channel.pipeline();

        // get the packet decoder from the pipeline
        Object decoder = pipeline.get(VANILLA_DECODER_KEY);

        // if the decoder is invalid
        if (decoder == null) {
            // return out of the method
            return;
        }

        // get the direction of the decoder
        Object direction = DECODER_DIRECTION.get(decoder);

        // if the direction of the decoder is invalid
        if (direction == null) {
            // return out of the method
            return;
        }

        // create the decoder
        liteDecoder = new LitePacketDecoder1_8(
                channel,
                this,
                this,
                this,
                direction
        );

        // replace the original decoder with the lite decoder
        pipeline.addBefore(
                VANILLA_DECODER_KEY,
                LITE_DECODER_KEY,
                liteDecoder
        );
    }

    /**
     * Initializes all the injection handlers
     */

    private void initializeHandlers() {
        // create a new channel registry handler
        channelRegister = new ChannelInitializer<Channel>() {

            @Override
            protected void initChannel(Channel channel) {
                try {
                    // attempt to inject to the channel
                    channel.eventLoop().execute(() -> {
                        // inject the decoder
                        injectDecoder(channel);

                        // inject the encoder
                        injectEncoder(channel);
                    });
                } catch (Exception e) {
                    // if something goes wrong log it to the console
                    System.err.println("Error injecting into channel " + channel.toString());
                }
            }

        };

        // create a mid point handler
        hackyRegister = new ChannelInitializer<Channel>() {
            @Override
            protected void initChannel(Channel channel) {
                // add the registry channel to the pipeline
                channel.pipeline().addLast(channelRegister);
            }
        };

        // create the channel adapter
        serverRegisterHandler = new ChannelInboundHandlerAdapter() {

            @Override
            public void channelRead(ChannelHandlerContext ctx, Object msg) {
                // get the current channel
                Channel channel = (Channel) msg;

                // add the mid point to the pipeline
                channel.pipeline().addFirst(hackyRegister);

                // fire the channel read with the channel
                ctx.fireChannelRead(msg);
            }
        };
    }

    /**
     * Adds a player connection
     * to the connection manager
     *
     * @param player player that you want to add the connection to
     */

    public void addConnection(Player player) {
        connections.add(new PlayerConnection(
                player,
                this
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

        // remove the protocol version from the cache
        protocolVersions.remove(player);

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
     * Updates players protocol version
     *
     * @param player   player that you want to update the version for
     * @param protocol protocol version that you want to update it to
     */

    public void setProtocol(Player player, int protocol) {
        protocolVersions.put(player, ProtocolVersion.getVersion(protocol));
    }

    /**
     * Gets the channels protocol version
     *
     * @param address address of the player
     * @return {@link Integer}
     */

    public int getProtocolVersion(SocketAddress address) {
        // create the player address
        String playerAddress = address.toString().substring(1).split(":")[0];

        // if the raw protocol does not contain the channel
        if (!rawProtocols.containsKey(playerAddress)) {
            // return -1 as the protocol version
            return -1;
        }

        // else return fetch the protocol version from the raw protocols
        return rawProtocols.get(playerAddress);
    }

    /**
     * Gets the players protocol version
     *
     * @param player player that you want to get the protocol from
     * @return {@link ProtocolVersion}
     */

    public ProtocolVersion getProtocolVersion(Player player) {
        return protocolVersions.get(player);
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

    /**
     * Gets the ServerConnection instance
     *
     * @param server spigot server that you want to get the handle from
     * @return ServerConnection
     */

    private Object getServerConnection(Server server) {
        return SERVER_CONNECTION.get(getMinecraftServer(server));
    }

    /**
     * Gets the MinecraftServer instance
     *
     * @param server spigot server that you want to get the handle from
     * @return MinecraftServer
     */

    private Object getMinecraftServer(Server server) {
        return SERVER_HANDLE.get(server);
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
