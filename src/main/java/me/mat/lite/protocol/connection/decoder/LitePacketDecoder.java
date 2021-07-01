package me.mat.lite.protocol.connection.decoder;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import lombok.Getter;
import lombok.Setter;
import me.mat.lite.protocol.connection.ClientHandshakeListener;
import me.mat.lite.protocol.connection.ClientPacketListener;
import me.mat.lite.protocol.connection.packet.LitePacket;
import me.mat.lite.protocol.connection.packet.LitePacketProvider;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;


public abstract class LitePacketDecoder<T> extends ByteToMessageDecoder {

    // used for storing the player protocol
    protected final Channel channel;

    // used for passing the handshake packets to the listener
    protected final ClientHandshakeListener clientHandshakeListener;

    // used for passing the packet calls to the packet listener
    protected final ClientPacketListener clientPacketListener;

    // used for keeping track of the packet direction
    @Getter
    protected final Object direction;

    // used for sending out the events
    @Setter
    protected Player player;

    public LitePacketDecoder(Channel channel, ClientHandshakeListener clientHandshakeListener, ClientPacketListener clientPacketListener, Object direction) {
        this.channel = channel;
        this.clientHandshakeListener = clientHandshakeListener;
        this.clientPacketListener = clientPacketListener;
        this.direction = direction;
    }

    public abstract Object processField(LitePacket packet, T serializer, Class<?> type);

    protected void invokeListeners(ChannelHandlerContext context, LitePacket packet) {
        // if the player instance is invalid
        if (player == null) {
            // get the channel
            Channel channel = context.channel();

            // invoke the client handshake listener
            clientHandshakeListener.onHandshakeReceive(channel.remoteAddress(), packet);

            // return out of the method
            return;
        }

        // else invoke the player packet listener
        clientPacketListener.onPacketReceive(player, packet);
    }

    /**
     * Processed all the fields
     * in the packet
     *
     * @param protocol   protocol of the packet
     * @param packetID   id of the packet
     * @param serializer serializer that the packet process will use
     * @return {@link LitePacket}
     */

    protected LitePacket process(String protocol, int packetID, T serializer) {
        // get the packet by id
        LitePacket packet = getPacket(protocol, packetID);

        // if the packet was not fetched
        if (packet == null) {

            // return out of the method
            return null;
        }

        // define the list that will hold all the objects
        List<Object> objects = new ArrayList<>();

        // loop through all the packet field types
        for (Class<?> type : packet.getTypes()) {
            // process the field
            Object value = processField(packet, serializer, type);

            // add the value to the objects
            objects.add(value);
        }

        // process the packet
        packet.process(objects.toArray());

        // return the packet
        return packet;
    }

    /**
     * Gets the LitePacket from the provided
     * protocol and packet id
     *
     * @param protocol protocol that the packet is assigned to
     * @param packetID id of the packet
     * @return {@link LitePacket}
     */

    protected LitePacket getPacket(String protocol, int packetID) {
        // attempt to fetch the packet class
        Class<?> packetCls = LitePacketProvider.getPacket(protocol, direction.toString(), packetID);

        // if the packet class was not fetched
        if (packetCls == null) {

            // return out of the method
            return null;
        }

        try {
            // create a new instance of the packet
            Object object = packetCls.newInstance();

            // if the object is an instance of the packet
            if (object instanceof LitePacket) {

                // return the packet
                return (LitePacket) object;
            }
        } catch (InstantiationException | IllegalAccessException e) {

            // if any exceptions occur return null
            return null;
        }

        // else just return null
        return null;
    }

}
