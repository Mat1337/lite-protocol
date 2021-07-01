package me.mat.lite.protocol.connection.packet.packets;

import me.mat.lite.protocol.connection.packet.LitePacket;

public class CloseWindowPacket extends LitePacket {

    public int id;

    public CloseWindowPacket() {
        super(byte.class);
    }

    @Override
    public void process(Object... values) {
        id = (byte) values[0];
    }

}
