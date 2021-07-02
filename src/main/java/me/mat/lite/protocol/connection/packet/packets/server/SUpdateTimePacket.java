package me.mat.lite.protocol.connection.packet.packets.server;

import me.mat.lite.protocol.connection.packet.LitePacket;

import java.util.Map;

public class SUpdateTimePacket extends LitePacket {

    public long totalWorldTime;
    public long worldTime;

    public SUpdateTimePacket() {
        super(long.class, long.class);
    }

    public SUpdateTimePacket(long totalWorldTime, long worldTime) {
        this.totalWorldTime = totalWorldTime;
        this.worldTime = worldTime;
    }

    @Override
    public void process(Object... values) {
        totalWorldTime = (long) values[0];
        worldTime = (long) values[1];
    }

    @Override
    public void process(Map<Object, Class<?>> data) {
        data.put(totalWorldTime, long.class);
        data.put(worldTime, long.class);
    }

}
