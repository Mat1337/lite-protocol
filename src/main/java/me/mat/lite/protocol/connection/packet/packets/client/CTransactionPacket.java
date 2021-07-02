package me.mat.lite.protocol.connection.packet.packets.client;

import lombok.ToString;
import me.mat.lite.protocol.connection.packet.LitePacket;

@ToString
public class CTransactionPacket extends LitePacket {

    public int windowId;
    public short uid;
    public boolean accepted;

    public CTransactionPacket() {
        super(byte.class, short.class, byte.class);
    }

    @Override
    public void process(Object... values) {
        windowId = (byte) values[0];
        uid = (short) values[1];
        accepted = (byte) values[2] != 0;
    }

}
