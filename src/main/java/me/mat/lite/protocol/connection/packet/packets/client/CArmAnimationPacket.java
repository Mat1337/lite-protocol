package me.mat.lite.protocol.connection.packet.packets.client;

import me.mat.lite.protocol.connection.packet.LitePacket;

public class CArmAnimationPacket extends LitePacket {

    public long timestamp;

    @Override
    public void process(Object... values) {
        timestamp = System.currentTimeMillis();
    }

}
