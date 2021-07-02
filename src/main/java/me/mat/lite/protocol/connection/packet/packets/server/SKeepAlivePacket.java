package me.mat.lite.protocol.connection.packet.packets.server;

import me.mat.lite.protocol.connection.packet.LitePacket;

import java.util.Map;

public class SKeepAlivePacket extends LitePacket {

    public int key;

    public SKeepAlivePacket() {
        super(int.class);
    }

    public SKeepAlivePacket(int key) {
        this.key = key;
    }

    @Override
    public void process(Object... values) {
        key = (int) values[0];
    }

    @Override
    public void process(Map<Object, Class<?>> data) {
        data.put(key, int.class);
    }

}
