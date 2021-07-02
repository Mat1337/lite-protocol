package me.mat.lite.protocol.connection.packet.protocol;

public interface PacketProtocolProvider {

    int getPacketID(String protocol, Object packet, String direction);

    Object[] listPackets(String protocol, String direction);

    String[] listProtocols();

}
