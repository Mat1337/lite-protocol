package me.mat.lite.protocol.connection.listener;

import me.mat.lite.protocol.connection.packet.LitePacket;

import java.net.SocketAddress;


public interface ClientLoginListener {

    boolean onLoginReceive(SocketAddress address, LitePacket packet);

}
