package me.mat.lite.protocol.connection.decoder;

import io.netty.handler.codec.ByteToMessageDecoder;
import lombok.Getter;
import me.mat.lite.protocol.connection.PlayerConnection;
import me.mat.lite.protocol.connection.packet.LitePacket;
import me.mat.lite.protocol.connection.packet.LitePacketProvider;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public abstract class LitePacketDecoder<T> extends ByteToMessageDecoder {

    // used for keeping track of the connections
    private final List<PlayerConnection> connections = new CopyOnWriteArrayList<>();

    // used for processing packets
    protected final PlayerConnection playerConnection;

    // used for keeping track of the packet direction
    @Getter
    protected final Object direction;

    public LitePacketDecoder(PlayerConnection playerConnection, Object direction) {
        this.playerConnection = playerConnection;
        this.direction = direction;
    }

    public abstract LitePacket process(int packetID, T serializer);

    public abstract Object processField(LitePacket packet, T serializer, Class<?> type);

    protected LitePacket getPacket(int packetID) {
        // attempt to fetch the packet class
        Class<?> packetCls = LitePacketProvider.CLIENT_PACKETS.get(packetID);

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
            return null;
        }

        return null;
    }

}
