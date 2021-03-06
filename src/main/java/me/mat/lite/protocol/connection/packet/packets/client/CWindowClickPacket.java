package me.mat.lite.protocol.connection.packet.packets.client;

import lombok.ToString;
import me.mat.lite.protocol.connection.packet.LitePacket;
import org.bukkit.inventory.ItemStack;

@ToString
public class CWindowClickPacket extends LitePacket {

    public int windowId;
    public int slot;
    public int button;
    public short actionNumber;
    public int shift;
    public ItemStack item;

    public CWindowClickPacket() {
        super(byte.class, short.class, byte.class, short.class, byte.class, ItemStack.class);
    }

    @Override
    public void process(Object... values) {
        windowId = (byte) values[0];
        slot = (short) values[1];
        button = (byte) values[2];
        actionNumber = (short) values[3];
        shift = (byte) values[4];
        item = (ItemStack) values[5];
    }

}
