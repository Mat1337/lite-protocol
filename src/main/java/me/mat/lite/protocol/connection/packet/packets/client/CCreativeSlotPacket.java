package me.mat.lite.protocol.connection.packet.packets.client;

import lombok.ToString;
import me.mat.lite.protocol.connection.packet.LitePacket;
import org.bukkit.inventory.ItemStack;

@ToString
public class CCreativeSlotPacket extends LitePacket {

    public int slot;
    public ItemStack stack;

    public CCreativeSlotPacket() {
        super(short.class, ItemStack.class);
    }

    @Override
    public void process(Object... values) {
        slot = (short) values[0];
        stack = (ItemStack) values[1];
    }

}
