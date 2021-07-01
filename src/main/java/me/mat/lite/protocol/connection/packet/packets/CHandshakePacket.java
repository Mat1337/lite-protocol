package me.mat.lite.protocol.connection.packet.packets;

import lombok.ToString;
import me.mat.lite.protocol.connection.packet.LitePacket;
import me.mat.lite.protocol.connection.packet.LitePacketProvider;
import me.mat.lite.protocol.util.UnsignedShort;

@ToString
public class CHandshakePacket extends LitePacket {

    public int protocolVersion;
    public String hostName;
    public int port;
    public LitePacketProvider.Protocol protocol;

    public CHandshakePacket() {
        super(int.class, String.class, UnsignedShort.class, int.class);
    }

    @Override
    public void process(Object... values) {
        protocolVersion = (int) values[0];
        hostName = (String) values[1];
        port = (int) values[2];
        protocol = LitePacketProvider.Protocol.values()[(int) values[3]];
    }

    @Override
    public int getStringLength() {
        return 32767;
    }

}
