package me.mat.lite.protocol.connection.packet.packets.server;

import me.mat.lite.protocol.connection.packet.LitePacket;

import java.util.Map;

public class SHeldItemSlot extends LitePacket {

    public int slot;

    public SHeldItemSlot() {
        super(byte.class);
    }

    public SHeldItemSlot(int slot) {
        this.slot = slot;
    }

    @Override
    public void process(Object... values) {
        slot = (int) values[0];
    }

    @Override
    public void process(Map<Object, Class<?>> data) {
        data.put(slot, byte.class);
    }

}
