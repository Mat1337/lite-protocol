package me.mat.lite.protocol.connection.packet.packets.client;

import lombok.ToString;
import me.mat.lite.protocol.connection.packet.LitePacket;

@ToString
public class CKeepAlivePacket extends LitePacket {

    public int key;

    public CKeepAlivePacket() {
        super(int.class);
    }

    @Override
    public void process(Object... values) {
        key = (int) values[0];
    }

}
