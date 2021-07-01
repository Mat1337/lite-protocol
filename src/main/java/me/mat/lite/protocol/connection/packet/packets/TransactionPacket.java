package me.mat.lite.protocol.connection.packet.packets;

import lombok.ToString;
import me.mat.lite.protocol.connection.packet.LitePacket;

@ToString
public class TransactionPacket extends LitePacket {

    public int windowId;
    public short uid;
    public boolean accepted;

    public TransactionPacket() {
        super(byte.class, short.class, byte.class);
    }

    @Override
    public void process(Object... values) {
        windowId = (byte) values[0];
        uid = (short) values[1];
        accepted = (byte) values[2] != 0;
    }

}
