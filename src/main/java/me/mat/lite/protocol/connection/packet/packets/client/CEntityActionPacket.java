package me.mat.lite.protocol.connection.packet.packets.client;

import me.mat.lite.protocol.connection.packet.LitePacket;

public class CEntityActionPacket extends LitePacket {

    public int entityID;
    public Action animation;
    public int auxData;

    public CEntityActionPacket() {
        super(int.class, Action.class, int.class);
    }

    @Override
    public void process(Object... values) {
        entityID = (int) values[0];
        animation = (Action) values[1];
        auxData = (int) values[2];
    }

    public enum Action {

        START_SNEAKING,
        STOP_SNEAKING,
        STOP_SLEEPING,
        START_SPRINTING,
        STOP_SPRINTING,
        RIDING_JUMP,
        OPEN_INVENTORY;

    }

}
