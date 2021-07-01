package me.mat.lite.protocol.connection.packet.packets;

import me.mat.lite.protocol.connection.packet.LitePacket;

public class CChatPacket extends LitePacket {

    public String message;

    public CChatPacket() {
        super(String.class);
    }

    @Override
    public void process(Object... values) {
        message = (String) values[0];
    }

}
