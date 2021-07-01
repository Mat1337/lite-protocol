package me.mat.lite.protocol.connection.decoder;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import lombok.Getter;
import lombok.Setter;
import me.mat.lite.protocol.connection.listener.ClientHandshakeListener;
import me.mat.lite.protocol.connection.listener.ClientLoginListener;
import me.mat.lite.protocol.connection.listener.ClientPacketListener;
import me.mat.lite.protocol.connection.packet.LitePacket;
import me.mat.lite.protocol.connection.packet.LitePacketProvider;
import me.mat.lite.protocol.connection.packet.packets.CHandshakePacket;
import org.bukkit.entity.Player;

import java.net.SocketAddress;
import java.util.ArrayList;
import java.util.List;


public abstract class LitePacketDecoder<T> extends ByteToMessageDecoder {

    // used for storing the player protocol
    protected final Channel channel;

    // used for passing the handshake packets to the listener
    protected final ClientHandshakeListener clientHandshakeListener;

    // used for listening for login packets
    protected final ClientLoginListener clientLoginListener;

    // used for passing the packet calls to the packet listener
    protected final ClientPacketListener clientPacketListener;

    // used for keeping track of the packet direction
    @Getter
    protected final Object direction;

    // used for sending out the events
    @Setter
    protected Player player;

    public LitePacketDecoder(Channel channel,
                             ClientHandshakeListener clientHandshakeListener,
                             ClientLoginListener clientLoginListener,
                             ClientPacketListener clientPacketListener,
                             Object direction) {
        this.channel = channel;
        this.clientHandshakeListener = clientHandshakeListener;
        this.clientLoginListener = clientLoginListener;
        this.clientPacketListener = clientPacketListener;
        this.direction = direction;
    }

    public abstract Object processField(LitePacket packet, T serializer, Class<?> type);

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
        return clientPacketListener.onPacketReceive(player, packet);
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
