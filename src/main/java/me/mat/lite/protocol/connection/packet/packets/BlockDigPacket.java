package me.mat.lite.protocol.connection.packet.packets;

import me.mat.lite.protocol.connection.packet.LitePacket;
import me.mat.lite.protocol.util.BlockPos;
import me.mat.lite.protocol.util.Direction;

public class BlockDigPacket extends LitePacket {

    public Type type;
    public BlockPos blockPos;
    public Direction direction;

    public BlockDigPacket() {
        super(Type.class, BlockPos.class, byte.class);
    }

    @Override
    public void process(Object... values) {
        type = (Type) values[0];
        blockPos = (BlockPos) values[1];
        direction = Direction.fromType1((short) values[2]);
    }

    public enum Type {

        START_DESTROY_BLOCK,
        ABORT_DESTROY_BLOCK,
        STOP_DESTROY_BLOCK,
        DROP_ALL_ITEMS,
        DROP_ITEM,
        RELEASE_USE_ITEM;

    }

}
