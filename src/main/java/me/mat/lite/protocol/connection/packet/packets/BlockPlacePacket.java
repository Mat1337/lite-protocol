package me.mat.lite.protocol.connection.packet.packets;

import me.mat.lite.protocol.connection.packet.LitePacket;
import me.mat.lite.protocol.util.BlockPos;
import org.bukkit.inventory.ItemStack;

public class BlockPlacePacket extends LitePacket {

    public BlockPos blockPos;
    public int placedBlockDirection;
    public ItemStack stack;
    public float facingX;
    public float facingY;
    public float facingZ;
    public long timestamp;

    public BlockPlacePacket() {
        super(BlockPos.class, byte.class, ItemStack.class, byte.class, byte.class, byte.class);
    }

    @Override
    public void process(Object... values) {
        timestamp = System.currentTimeMillis();
        blockPos = (BlockPos) values[0];
        placedBlockDirection = (short) values[1];
        stack = (ItemStack) values[2];
        facingX = (short) values[3] / 16.0f;
        facingY = (short) values[4] / 16.0f;
        facingZ = (short) values[5] / 16.0f;
    }

}
