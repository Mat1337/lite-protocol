package me.mat.lite.protocol.connection.packet.packets.server;

import me.mat.lite.protocol.connection.packet.LitePacket;

import java.util.Map;

public class SKeepAlivePacket extends LitePacket {

    public int key;

    public SKeepAlivePacket(int key) {
        this.key = key;
    }

    @Override
    public void process(Map<Class<?>, Object> data) {
        data.put(int.class, key);
    }

}
