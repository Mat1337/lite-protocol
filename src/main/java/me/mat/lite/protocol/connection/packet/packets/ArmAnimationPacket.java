package me.mat.lite.protocol.connection.packet.packets;

import me.mat.lite.protocol.connection.packet.LitePacket;

public class ArmAnimationPacket extends LitePacket {

    public long timestamp;

    @Override
    public void process(Object... values) {
        timestamp = System.currentTimeMillis();
    }

}
