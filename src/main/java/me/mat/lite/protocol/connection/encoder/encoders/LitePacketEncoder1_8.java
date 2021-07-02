package me.mat.lite.protocol.connection.encoder.encoders;

import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import me.mat.lite.protocol.connection.encoder.LitePacketEncoder;
import me.mat.lite.protocol.connection.listener.ClientHandshakeListener;
import me.mat.lite.protocol.connection.listener.ClientLoginListener;
import me.mat.lite.protocol.connection.listener.ServerPacketListener;
import me.mat.lite.protocol.connection.packet.LitePacket;
import net.minecraft.server.v1_8_R3.*;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class LitePacketEncoder1_8 extends LitePacketEncoder<Packet<?>> {

    public LitePacketEncoder1_8(Channel channel, ClientHandshakeListener clientHandshakeListener, ClientLoginListener clientLoginListener, ServerPacketListener serverPacketListener, Object direction) {
        super(channel, clientHandshakeListener, clientLoginListener, serverPacketListener, direction);
    }

    @Override
    public void process(Object object, Class<?> type, Object value) {
        PacketDataSerializer serializer = (PacketDataSerializer) object;
        if (type.equals(byte.class)) {
            serializer.writeByte((int) value);
        } else if (type.equals(int.class)) {
            serializer.b((int) value);
        } else if (type.equals(long.class)) {
            serializer.writeLong((long) value);
        }
    }

    @Override
    protected void encode(ChannelHandlerContext channelHandlerContext, Packet packet, ByteBuf byteBuf) throws Exception {
        EnumProtocol protocol = channelHandlerContext.channel().attr(NetworkManager.c).get();
        Integer packetID = protocol.a((EnumProtocolDirection) direction, packet);
        if (packetID == null) {
            throw new IOException("Can't serialize unregistered packet");
        } else {
            PacketDataSerializer serializer = new PacketDataSerializer(byteBuf);
            serializer.b(packetID);

            LitePacket litePacket = getPacket(packet.getClass());
            if (litePacket != null) {
                Map<Object, Class<?>> dataMap = new HashMap<>();
                litePacket.process(dataMap);
                dataMap.forEach((value, type) -> process(serializer, type, value));
            } else {
                try {
                    packet.b(serializer);
                } catch (Throwable e) {
                    e.printStackTrace();
                }
            }
        }
    }

}
