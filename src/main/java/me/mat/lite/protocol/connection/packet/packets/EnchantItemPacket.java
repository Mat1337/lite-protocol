package me.mat.lite.protocol.connection.packet.packets;

import lombok.ToString;
import me.mat.lite.protocol.connection.packet.LitePacket;

@ToString
public class EnchantItemPacket extends LitePacket {

    public int windowId;
    public int button;

    public EnchantItemPacket() {
        super(byte.class, byte.class);
    }

    @Override
    public void process(Object... values) {
        windowId = (byte) values[0];
        button = (byte) values[1];
    }

}
