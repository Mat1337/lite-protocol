package me.mat.lite.protocol.connection.encoder.encoders;

import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import me.mat.lite.protocol.connection.encoder.LitePacketEncoder;
import net.minecraft.server.v1_8_R3.*;

import java.io.IOException;

public class LitePacketEncoder1_8 extends LitePacketEncoder<Packet<?>> {

    public LitePacketEncoder1_8(Channel channel, Object direction) {
        super(channel, direction);
    }

    @Override
    protected void encode(ChannelHandlerContext channelHandlerContext, Packet packet, ByteBuf byteBuf) throws Exception {
        Integer var4 = channelHandlerContext.channel().attr(NetworkManager.c).get().a((EnumProtocolDirection) direction, packet);
        if (var4 == null) {
            throw new IOException("Can't serialize unregistered packet");
        } else {
            PacketDataSerializer var5 = new PacketDataSerializer(byteBuf);
            var5.b(var4);
            try {
                packet.b(var5);
            } catch (Throwable e) {
                e.printStackTrace();
            }
        }
    }

}
