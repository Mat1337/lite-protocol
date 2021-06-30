package me.mat.lite.protocol.connection.packet.packets;

import me.mat.lite.protocol.connection.packet.LitePacket;

public class ChatPacket extends LitePacket {

    public String message;

    public ChatPacket() {
        super(String.class);
    }

    @Override
    public void process(Object... values) {
        message = (String) values[0];
    }

}
