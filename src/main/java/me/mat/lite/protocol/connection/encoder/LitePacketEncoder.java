package me.mat.lite.protocol.connection.encoder;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import lombok.Setter;
import me.mat.lite.protocol.connection.listener.ClientHandshakeListener;
import me.mat.lite.protocol.connection.listener.ClientLoginListener;
import me.mat.lite.protocol.connection.listener.ServerPacketListener;
import me.mat.lite.protocol.connection.packet.LitePacket;
import me.mat.lite.protocol.connection.packet.LitePacketProvider;
import org.bukkit.entity.Player;

import java.net.SocketAddress;
import java.util.HashMap;
import java.util.Map;


public abstract class LitePacketEncoder<T> extends MessageToByteEncoder<T> {

    protected final Channel channel;
    protected final Object direction;

    protected final ClientHandshakeListener clientHandshakeListener;
    protected final ClientLoginListener clientLoginListener;
    protected final ServerPacketListener serverPacketListener;

    // used for queuing and sending packets from the server to the client
    private final Map<Class<?>, LitePacket> sendQueue;

    // used for sending out the events
    @Setter
    protected Player player;

    public LitePacketEncoder(Channel channel,
                             ClientHandshakeListener clientHandshakeListener,
                             ClientLoginListener clientLoginListener,
                             ServerPacketListener serverPacketListener,
                             Object direction) {
        this.channel = channel;
        this.clientHandshakeListener = clientHandshakeListener;
        this.clientLoginListener = clientLoginListener;
        this.serverPacketListener = serverPacketListener;
        this.direction = direction;
        this.sendQueue = new HashMap<>();
    }

    public abstract void process(Object object, Class<?> type, Object value);

    /**
     * Invokes the correct listener
     * for the current packet
     *
     * @param context   channel context
     * @param packet    packet that is being sent
     * @param handShake flag containing if its a handshake protocol
     * @param login     flag containing if its a login protocol
     * @return {@link Boolean}
     */

    protected boolean invokeListeners(ChannelHandlerContext context, LitePacket packet, boolean handShake, boolean login) {
        // get the channel
        Channel channel = context.channel();

        // get the remote address
        SocketAddress address = channel.remoteAddress();

        // if the player instance is invalid
        if (handShake) {

            // invoke the client handshake listener
            return clientHandshakeListener.onHandshakeReceive(address, packet);
        } else if (login) {

            // invoke the client login listener
            return clientLoginListener.onLoginReceive(address, packet);
        }

        // else invoke the player packet listener
        return serverPacketListener.onPacketSend(player, packet);
    }

    /**
     * Gets the packet from the send queue
     * that matches the nms packet class
     *
     * @param nmsPacket nms packet that you want to search for
     * @return {@link LitePacket}
     */

    protected LitePacket getPacket(Class<?> nmsPacket) {
        // if the send queue does not contain that packet
        if (!sendQueue.containsKey(nmsPacket)) {
            // return null
            return null;
        }

        // else get the packet
        LitePacket packet = sendQueue.get(nmsPacket);

        // and remove the packet from the send queue
        sendQueue.remove(nmsPacket);

        // return it
        return packet;
    }

    /**
     * Adds a packet to the send queue
     *
     * @param packet packet that you want to add to send queue
     * @return {@link Class}
     */

    public Class<?> addToServerSendQueue(LitePacket packet) {
        // get the lite packet class
        Class<?> packetCls = packet.getClass();

        // if the reverse packet lookup does not contain the packet
        if (!LitePacketProvider.REVERSE_PACKET_LOOKUP.containsKey(packetCls)) {
            // return out of the method
            return null;
        }

        // get the minecraft packet
        Class<?> nmsPacket = LitePacketProvider.REVERSE_PACKET_LOOKUP.get(packetCls);

        // add the packet to the server send queue
        sendQueue.put(nmsPacket, packet);

        // return the nms packet
        return nmsPacket;
    }

}
