package me.mat.lite.protocol.connection.decoder.decoders;

import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import me.mat.lite.protocol.connection.PacketHandler;
import me.mat.lite.protocol.connection.decoder.LitePacketDecoder;
import me.mat.lite.protocol.connection.packet.LitePacket;
import me.mat.lite.protocol.connection.packet.packets.CHandshakePacket;
import me.mat.lite.protocol.util.BlockPos;
import me.mat.lite.protocol.util.UnsignedByte;
import me.mat.lite.protocol.util.UnsignedShort;
import me.mat.lite.protocol.util.vector.Vec3f;
import net.minecraft.server.v1_8_R3.*;
import org.bukkit.craftbukkit.v1_8_R3.inventory.CraftItemStack;
import org.bukkit.inventory.ItemStack;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class LitePacketDecoder1_8 extends LitePacketDecoder<PacketDataSerializer> {

    public LitePacketDecoder1_8(Channel channel, PacketHandler packetHandler, Object direction, int id) {
        super(channel, packetHandler, direction, id);
    }

    @Override
    public LitePacket process(String protocol, int packetID, PacketDataSerializer serializer) {
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

    @Override
    public Object processField(LitePacket packet, PacketDataSerializer serializer, Class<?> type) {
        if (serializer.readableBytes() == 0) {
            return null;
        }

        if (type.equals(byte.class)) {
            return serializer.readByte();
        } else if (type.equals(UnsignedByte.class)) {
            return serializer.readUnsignedByte();
        } else if (type.equals(short.class)) {
            return serializer.readShort();
        } else if (type.equals(UnsignedShort.class)) {
            return serializer.readUnsignedShort();
        } else if (type.equals(int.class)) {
            return serializer.e();
        } else if (type.equals(float.class)) {
            return serializer.readFloat();
        } else if (type.equals(double.class)) {
            return serializer.readDouble();
        } else if (type.equals(long.class)) {
            return serializer.readLong();
        } else if (type.equals(boolean.class)) {
            return serializer.readUnsignedByte() != 0;
        } else if (type.equals(String.class)) {
            return serializer.c(packet.getStringLength());
        } else if (type.equals(Vec3f.class)) {
            return new Vec3f(
                    serializer.readFloat(),
                    serializer.readFloat(),
                    serializer.readFloat()
            );
        } else if (type.equals(BlockPos.class)) {
            BlockPosition blockPosition
                    = serializer.c();
            return new BlockPos(
                    blockPosition.getX(),
                    blockPosition.getY(),
                    blockPosition.getZ()
            );
        } else if (type.equals(ItemStack.class)) {
            try {
                net.minecraft.server.v1_8_R3.ItemStack stack = serializer.i();
                return CraftItemStack.asBukkitCopy(stack);
            } catch (IOException e) {
                return null;
            }
        } else if (type.equals(byte[].class)) {
            return serializer.a();
        } else {
            if (type.getSuperclass() != null
                    && type.getSuperclass().equals(Enum.class)) {
                return ((Enum[]) type.getEnumConstants())[serializer.e()];
            }
        }
        return null;
    }

    @Override
    protected void decode(ChannelHandlerContext channelHandlerContext, ByteBuf byteBuf, List<Object> list) throws Exception {
        if (byteBuf.readableBytes() != 0) {
            PacketDataSerializer dataSerializer = new PacketDataSerializer(byteBuf);
            PacketDataSerializer packetDataSerializer = new PacketDataSerializer(byteBuf.duplicate());
            int packetID = dataSerializer.e();
            EnumProtocol protocol = channelHandlerContext.channel().attr(NetworkManager.c).get();
            Packet<?> packet = protocol.a((EnumProtocolDirection) direction, packetID);
            if (packet == null) {
                throw new IOException("Bad packet id " + packetID);
            } else {
                packet.a(dataSerializer);
                if (dataSerializer.readableBytes() > 0) {
                    throw new IOException("Packet " + channelHandlerContext.channel().attr(NetworkManager.c).get().a() + "/" + packetID + " (" + packet.getClass().getSimpleName() + ") was larger than I expected, found " + dataSerializer.readableBytes() + " bytes extra whilst reading packet " + packetID);
                } else {
                    list.add(packet);
                    LitePacket litePacket = process(protocol.toString(), packetDataSerializer.e(), packetDataSerializer);
                    if (litePacket != null) {
                        if (litePacket instanceof CHandshakePacket) {
                            CHandshakePacket handshakePacket = (CHandshakePacket) litePacket;
                        }
                        if (player != null) {
                            packetHandler.onPacketReceive(player, litePacket);
                        }
                    }
                }
            }
        }
    }

}
