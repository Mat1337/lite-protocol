package me.mat.lite.protocol.connection;

import io.netty.channel.Channel;
import io.netty.channel.ChannelPipeline;
import lombok.Getter;
import me.mat.lite.protocol.connection.decoder.LitePacketDecoder;
import me.mat.lite.protocol.connection.decoder.decoders.LitePacketDecoder1_8;
import me.mat.lite.protocol.connection.encoder.LitePacketEncoder;
import me.mat.lite.protocol.connection.encoder.encoders.LitePacketEncoder1_8;
import me.mat.lite.protocol.util.ReflectionUtil;
import me.mat.lite.protocol.util.accessor.accessors.FieldAccessor;
import me.mat.lite.protocol.util.accessor.accessors.MethodAccessor;
import org.bukkit.entity.Player;

@Getter
public class PlayerConnection {

    // used for accessing the EntityPlayer handle
    private static final MethodAccessor GET_HANDLE = new MethodAccessor(
            ReflectionUtil.getBukkitEntityClass("CraftPlayer"),
            "getHandle"
    );

    // used for accessing the PlayerConnection handle
    private static final FieldAccessor PLAYER_CONNECTION = new FieldAccessor(
            ReflectionUtil.getMinecraftClass("EntityPlayer"),
            "playerConnection"
    );

    // used for accessing the NetworkManager handle
    private static final FieldAccessor NETWORK_MANAGER
            = new FieldAccessor(
            ReflectionUtil.getMinecraftClass("PlayerConnection"),
            "networkManager"
    );

    // used for accessing the Channel handle
    private static final FieldAccessor CHANNEL = new FieldAccessor(
            ReflectionUtil.getMinecraftClass("NetworkManager"),
            "channel"
    );

    // used for accessing the EnumProtocolDirection from the PacketDecoder
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

    private final Player player;
    private final ConnectionManager connectionManager;

    private LitePacketDecoder<?> liteDecoder;
    private LitePacketEncoder<?> liteEncoder;

    public PlayerConnection(Player player, ConnectionManager connectionManager) {
        this.player = player;
        this.connectionManager = connectionManager;

        // open the connection
        this.open();

        player.sendMessage("Your protocol version: " + connectionManager.getProtocolVersion(player));
    }

    /**
     * Opens the connection to the player
     */

    public void open() {
        // get the channel
        Channel channel = getChannel();

        // get the protocol version based on the address
        int protocol = connectionManager.getProtocolVersion(channel.remoteAddress());

        // update players protocol version
        connectionManager.setProtocol(player, protocol);

        // open the connection to the player
        channel.eventLoop().execute(() -> open(channel));
    }

    /**
     * Opens the connection to the player
     */

    private void open(Channel channel) {
        // get the channel pipeline
        ChannelPipeline pipeline = channel.pipeline();

        // get the vanilla decoder
        Object vanillaDecoder = pipeline.get(ConnectionManager.VANILLA_DECODER_KEY);

        // if the vanilla decoder valid
        if (vanillaDecoder != null) {

            // get the direction from the vanilla decoder
            Object direction = DECODER_DIRECTION.get(vanillaDecoder);

            // if the direction is valid
            if (direction != null) {

                // get the decoder
                liteDecoder = (LitePacketDecoder<?>) pipeline.get(ConnectionManager.LITE_DECODER_KEY);

                // if the lite decoder is invalid
                if (liteDecoder == null) {
                    // else create the lite decoder
                    liteDecoder = new LitePacketDecoder1_8(
                            channel,
                            connectionManager,
                            connectionManager,
                            connectionManager,
                            direction
                    );

                    // add the lite encoder before the vanilla encoder
                    pipeline.addBefore(
                            ConnectionManager.VANILLA_DECODER_KEY,
                            ConnectionManager.LITE_DECODER_KEY,
                            liteDecoder
                    );
                }

                // set the lite decoders player instance
                liteDecoder.setPlayer(player);
            }
        }

        // get the vanilla encoder
        Object vanillaEncoder = pipeline.get(ConnectionManager.VANILLA_ENCODER_KEY);

        // if the vanilla encoder valid
        if (vanillaEncoder != null) {

            // get the direction from the vanilla encoder
            Object direction = ENCODER_DIRECTION.get(vanillaEncoder);

            // if the direction is valid
            if (direction != null) {

                // get the encoder
                liteEncoder = (LitePacketEncoder<?>) pipeline.get(ConnectionManager.LITE_ENCODER_KEY);

                // if the lite decoder is invalid
                if (liteEncoder == null) {

                    // create the lite encoder
                    liteEncoder = new LitePacketEncoder1_8(
                            channel,
                            direction
                    );

                    // add the lite encoder after the vanilla encoder
                    pipeline.addAfter(
                            ConnectionManager.VANILLA_ENCODER_KEY,
                            ConnectionManager.LITE_ENCODER_KEY,
                            liteEncoder
                    );
                }
            }
        }
    }

    /**
     * Closes the connection to the player
     */

    public void close() {
        // get the channel
        Channel channel = getChannel();

        // if the channel is invalid
        if (channel == null) {
            // return out of the method
            return;
        }

        // else close the channel
        channel.eventLoop().execute(() -> close(channel));
    }

    /**
     * Closes the connection to the player
     *
     * @param channel channel that the connection is on
     */

    private void close(Channel channel) {
        // if the lite decoder instance is invalid
        if (liteDecoder == null) {
            // return out of the method
            return;
        }

        // get the channel pipeline
        ChannelPipeline pipeline = channel.pipeline();

        // get the lite decoder
        Object liteDecoder
                = pipeline.get(ConnectionManager.LITE_DECODER_KEY);

        // if the lite decoder is valid
        if (liteDecoder != null) {
            // else remove the lite decoder from the pipeline
            pipeline.remove(ConnectionManager.LITE_DECODER_KEY);
        }

        // get the lite encoder
        Object liteEncoder =
                pipeline.get(ConnectionManager.LITE_ENCODER_KEY);

        // if the lite encoder is valid
        if (liteEncoder != null) {
            // else remove the lite encoder from the pipeline
            pipeline.remove(ConnectionManager.LITE_ENCODER_KEY);
        }
    }

    /**
     * Gets the Channel handle
     * from the NetworkManager instance
     *
     * @return {@link Channel}
     */

    private Channel getChannel() {
        return CHANNEL.get(getNetworkManager());
    }

    /**
     * Gets the NetworkManager handle
     * from the PlayerConnection instance
     *
     * @return NetworkManager handle
     */

    private Object getNetworkManager() {
        return NETWORK_MANAGER.get(getPlayerConnection());
    }

    /**
     * Gets the PlayerConnection handle
     * from the EntityPlayer instance
     *
     * @return PlayerConnection handle
     */

    private Object getPlayerConnection() {
        return PLAYER_CONNECTION.get(getEntityPlayer());
    }

    /**
     * Gets the EntityPlayer handle
     * from the spigot Player instance
     *
     * @return EntityPlayer handle
     */

    private Object getEntityPlayer() {
        return GET_HANDLE.invoke(player);
    }

}
