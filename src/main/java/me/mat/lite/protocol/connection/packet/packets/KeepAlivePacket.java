package me.mat.lite.protocol.connection.packet.packets;

import me.mat.lite.protocol.connection.packet.LitePacket;

public class KeepAlivePacket extends LitePacket {

    public int entityID;

    public KeepAlivePacket() {
        super(int.class);
    }

    @Override
    public void process(Object... values) {
        entityID = (int) values[0];
    }

}
