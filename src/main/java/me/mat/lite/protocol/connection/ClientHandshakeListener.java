package me.mat.lite.protocol.connection;

import me.mat.lite.protocol.connection.packet.LitePacket;

import java.net.SocketAddress;

public interface ClientHandshakeListener {

    boolean onHandshakeReceive(SocketAddress address, LitePacket packet);

}
