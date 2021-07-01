package me.mat.lite.protocol.connection.decoder;

import io.netty.channel.Channel;
import io.netty.handler.codec.ByteToMessageDecoder;
import lombok.Getter;
import lombok.Setter;
import me.mat.lite.protocol.connection.PacketHandler;
import me.mat.lite.protocol.connection.packet.LitePacket;
import me.mat.lite.protocol.connection.packet.LitePacketProvider;
import me.mat.lite.protocol.util.ReflectionUtil;
import org.bukkit.entity.Player;


public abstract class LitePacketDecoder<T> extends ByteToMessageDecoder {

    private static final Class<?> HANDSHAKE = ReflectionUtil.getMinecraftClass("PacketHandshakingInSetProtocol");
    private static final Class<?> STATUS = ReflectionUtil.getMinecraftClass("PacketStatusInStart");

    // used for storing the player protocol
    protected final Channel channel;

    // used for processing packets
    protected final PacketHandler packetHandler;

    // used for keeping track of the packet direction
    @Getter
    protected final Object direction;

    // used for keeping track of player ids
    @Getter
    protected final int id;

    // used for sending out the events
    @Setter
    protected Player player;

    public LitePacketDecoder(Channel channel, PacketHandler packetHandler, Object direction, int id) {
        this.channel = channel;
        this.packetHandler = packetHandler;
        this.direction = direction;
        this.id = id;
    }

    public abstract LitePacket process(String protocol, int packetID, T serializer);

    public abstract Object processField(LitePacket packet, T serializer, Class<?> type);

    protected boolean isStatus(Class<?> cls) {
        return STATUS.equals(cls);
    }

    protected boolean isHandShake(Class<?> cls) {
        return HANDSHAKE.equals(cls);
    }

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
            return null;
        }

        return null;
    }

}
