package me.mat.lite.protocol.connection.encoder;

import io.netty.channel.Channel;
import io.netty.handler.codec.MessageToByteEncoder;
import me.mat.lite.protocol.connection.packet.LitePacket;
import me.mat.lite.protocol.connection.packet.LitePacketProvider;

import java.util.HashMap;
import java.util.Map;


public abstract class LitePacketEncoder<T> extends MessageToByteEncoder<T> {

    protected final Channel channel;
    protected final Object direction;

    // used for queuing and sending packets from the server to the client
    private final Map<Class<?>, LitePacket> sendQueue;

    public LitePacketEncoder(Channel channel, Object direction) {
        this.channel = channel;
        this.direction = direction;
        this.sendQueue = new HashMap<>();
    }

    public abstract void process(Object object, Class<?> type, Object value);

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
