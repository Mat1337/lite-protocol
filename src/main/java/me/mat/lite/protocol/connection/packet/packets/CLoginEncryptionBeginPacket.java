package me.mat.lite.protocol.connection.packet.packets;

import me.mat.lite.protocol.connection.packet.LitePacket;

public class CLoginEncryptionBeginPacket extends LitePacket {

    private byte[] secretKeyEncrypted;
    private byte[] verifyTokenEncrypted;

    public CLoginEncryptionBeginPacket() {
        super(byte[].class, byte[].class);
    }

    @Override
    public void process(Object... values) {
        secretKeyEncrypted = (byte[]) values[0];
        verifyTokenEncrypted = (byte[]) values[1];
    }

}
