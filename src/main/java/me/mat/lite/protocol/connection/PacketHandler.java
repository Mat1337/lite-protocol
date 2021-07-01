package me.mat.lite.protocol.connection;

import me.mat.lite.protocol.connection.packet.LitePacket;
import org.bukkit.entity.Player;

public interface PacketHandler {

    void onPacketReceive(Player player, LitePacket packet);

    void onPacketSend(Player player, LitePacket packet);

}
