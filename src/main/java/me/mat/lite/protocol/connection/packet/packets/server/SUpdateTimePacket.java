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
    public void process(Map<Object, Class<?>> data) {
        data.put(totalWorldTime, long.class);
        data.put(worldTime, long.class);
    }

}
