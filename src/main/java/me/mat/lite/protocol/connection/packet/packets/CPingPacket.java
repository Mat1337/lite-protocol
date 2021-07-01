package me.mat.lite.protocol.connection.packet.packets;

import lombok.ToString;
import me.mat.lite.protocol.connection.packet.LitePacket;

@ToString
public class CPingPacket extends LitePacket {

    private long time;

    public CPingPacket() {
        super(long.class);
    }

    @Override
    public void process(Object... values) {
        time = (long) values[0];
    }

}
