package me.mat.lite.protocol.connection.packet.packets.client;

import me.mat.lite.protocol.connection.packet.LitePacket;

public class CHeldItemSlotPacket extends LitePacket {

    public int slot;

    public CHeldItemSlotPacket() {
        super(short.class);
    }

    @Override
    public void process(Object... values) {
        slot = (short) values[0];
    }

}
