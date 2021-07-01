package me.mat.lite.protocol.connection.packet.packets;

import me.mat.lite.protocol.connection.packet.LitePacket;

public class CCloseWindowPacket extends LitePacket {

    public int id;

    public CCloseWindowPacket() {
        super(byte.class);
    }

    @Override
    public void process(Object... values) {
        id = (byte) values[0];
    }

}
