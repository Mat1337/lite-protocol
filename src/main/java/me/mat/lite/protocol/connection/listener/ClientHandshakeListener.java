package me.mat.lite.protocol.connection.listener;

import io.netty.channel.Channel;
import me.mat.lite.protocol.connection.packet.LitePacket;
import org.bukkit.entity.Player;

import java.net.SocketAddress;

public interface ClientHandshakeListener {

    boolean onHandshakeReceive(SocketAddress address, LitePacket packet);

    void setProtocol(Channel channel, int protocol);

    void setProtocol(Player player, int protocol);

}
