package me.mat.lite.protocol.connection;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler;
import lombok.Getter;
import me.mat.lite.protocol.connection.decoder.LitePacketDecoder;
import me.mat.lite.protocol.connection.decoder.decoders.LitePacketDecoder1_8;
import me.mat.lite.protocol.util.ReflectionUtil;
import me.mat.lite.protocol.util.accessor.accessors.ConstructorAccessor;
import me.mat.lite.protocol.util.accessor.accessors.FieldAccessor;
import me.mat.lite.protocol.util.accessor.accessors.MethodAccessor;
import org.bukkit.entity.Player;

@Getter
public class PlayerConnection {

    private static final MethodAccessor GET_HANDLE = new MethodAccessor(
            ReflectionUtil.getBukkitEntityClass("CraftPlayer"),
            "getHandle"
    );

    private static final FieldAccessor PLAYER_CONNECTION = new FieldAccessor(
            ReflectionUtil.getMinecraftClass("EntityPlayer"),
            "playerConnection"
    );

    private static final FieldAccessor NETWORK_MANAGER
            = new FieldAccessor(
            ReflectionUtil.getMinecraftClass("PlayerConnection"),
            "networkManager"
    );

    private static final FieldAccessor CHANNEL = new FieldAccessor(
            ReflectionUtil.getMinecraftClass("NetworkManager"),
            "channel"
    );

    private static final FieldAccessor DIRECTION = new FieldAccessor(
            ReflectionUtil.getMinecraftClass("PacketDecoder"),
            ReflectionUtil.getMinecraftClass("EnumProtocolDirection"),
            0
    );

    private static final ConstructorAccessor PACKET_DECODER = new ConstructorAccessor(
            ReflectionUtil.getMinecraftClass("PacketDecoder"),
            ReflectionUtil.getMinecraftClass("EnumProtocolDirection")
    );

    private LitePacketDecoder<?> liteDecoder;
    private final ConnectionManager connectionManager;
    private final Player player;
    private final int id;

    public PlayerConnection(LitePacketDecoder<?> liteDecoder, ConnectionManager connectionManager, Player player, int id) {
        this.liteDecoder = liteDecoder;
        this.connectionManager = connectionManager;
        this.player = player;
        this.id = id;

        // inject the channel handler
        this.load();
    }

    private void load() {
        // get the channel
        Channel channel = getChannel();

        // if the channel is invalid
        if (channel == null) {

            // return out of the method
            return;
        }

        // execute the inject method
        channel.eventLoop().execute(() -> inject(channel));
    }

    private void inject(Channel channel) {
        channel.eventLoop().execute(() -> {
            Object decoder = channel.pipeline().get("decoder");
            if (decoder != null) {
                if (!LitePacketDecoder.class.isInstance(decoder)) {
                    LitePacketDecoder1_8 d = new LitePacketDecoder1_8(channel, connectionManager, liteDecoder.getDirection(), liteDecoder.getId());
                    channel.pipeline().replace("decoder", "decoder", d);
                    liteDecoder = d;
                }
                liteDecoder.setPlayer(player);
                decoder = channel.pipeline().get("decoder");
                player.sendMessage(decoder.getClass().getName());
            }
        });
    }

    public void close() {
        // get the channel
        Channel channel = getChannel();

        // if the channel is invalid
        if (channel == null) {

            // return out of the method
            return;
        }

        // create a vanilla packet decoder
        ChannelHandler packetDecoder
                = PACKET_DECODER.newInstance(liteDecoder.getDirection());

        // remove the handler from the channel
        channel.eventLoop().execute(() -> {

            // if the pipeline does not contain a decoder
            if (channel.pipeline().get("decoder") == null) {

                // return out of the method
                return;
            }

            // replace the pipeline decoder with vanilla decoder
            channel.pipeline().replace(
                    "decoder",
                    "decoder",
                    packetDecoder
            );
        });
    }

    private Channel getChannel() {
        return CHANNEL.get(getNetworkManager());
    }

    private Object getNetworkManager() {
        return NETWORK_MANAGER.get(getPlayerConnection());
    }

    private Object getPlayerConnection() {
        return PLAYER_CONNECTION.get(getEntityPlayer());
    }

    private Object getEntityPlayer() {
        return GET_HANDLE.invoke(player);
    }

}
