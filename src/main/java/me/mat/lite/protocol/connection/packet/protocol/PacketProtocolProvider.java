package me.mat.lite.protocol.connection.packet.protocol;

public interface PacketProtocolProvider {

    Object[] listPackets(String protocol, String direction);

    String[] listProtocols();

}
