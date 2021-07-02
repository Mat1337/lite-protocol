package me.mat.lite.protocol.connection.packet.packets.client;

import com.mojang.authlib.GameProfile;
import lombok.ToString;
import me.mat.lite.protocol.connection.packet.LitePacket;

@ToString
public class CLoginStartPacket extends LitePacket {

    public GameProfile gameProfile;

    public CLoginStartPacket() {
        super(String.class);
    }

    @Override
    public void process(Object... values) {
        gameProfile = new GameProfile(null, (String) values[0]);
    }

    @Override
    public int getStringLength() {
        return 16;
    }

}
