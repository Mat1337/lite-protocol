package me.mat.lite.protocol.connection.listener;

import me.mat.lite.protocol.connection.packet.LitePacket;
import org.bukkit.entity.Player;

public interface ServerPacketListener {

    boolean onPacketSend(Player player, LitePacket packet);

}
