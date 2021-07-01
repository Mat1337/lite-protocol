package me.mat.lite.protocol.connection.packet.packets;

import lombok.ToString;
import me.mat.lite.protocol.connection.packet.LitePacket;
import org.bukkit.inventory.ItemStack;

@ToString
public class CreativeSlotPacket extends LitePacket {

    public int slot;
    public ItemStack stack;

    public CreativeSlotPacket() {
        super(short.class, ItemStack.class);
    }

    @Override
    public void process(Object... values) {
        slot = (short) values[0];
        stack = (ItemStack) values[1];
    }

}
