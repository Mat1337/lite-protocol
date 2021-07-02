package me.mat.lite.protocol.connection.packet.packets.client;

import me.mat.lite.protocol.connection.packet.LitePacket;
import me.mat.lite.protocol.util.BlockPos;
import me.mat.lite.protocol.util.Direction;
import me.mat.lite.protocol.util.UnsignedByte;

public class CBlockDigPacket extends LitePacket {

    public Type type;
    public BlockPos blockPos;
    public Direction direction;

    public CBlockDigPacket() {
        super(Type.class, BlockPos.class, UnsignedByte.class);
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
