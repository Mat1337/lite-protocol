package me.mat.lite.protocol.connection.packet.packets;

import me.mat.lite.protocol.connection.packet.LitePacket;

public class HeldItemSlotPacket extends LitePacket {

    public int slot;

    public HeldItemSlotPacket() {
        super(short.class);
    }

    @Override
    public void process(Object... values) {
        slot = (short) values[0];
    }

}
