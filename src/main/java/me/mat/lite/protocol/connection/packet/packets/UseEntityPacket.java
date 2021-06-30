package me.mat.lite.protocol.connection.packet.packets;

import me.mat.lite.protocol.connection.packet.LitePacket;
import me.mat.lite.protocol.util.vector.Vec3f;

public class UseEntityPacket extends LitePacket {

    public int entityID;
    public Action action;
    public Vec3f look;

    public UseEntityPacket() {
        super(int.class, Action.class, Vec3f.class);
    }

    @Override
    public void process(Object... values) {
        entityID = (int) values[0];
        action = (Action) values[1];
        if (action.equals(Action.INTERACT_AT)) {
            look = (Vec3f) values[2];
        }
    }

    public enum Action {

        INTERACT,
        ATTACK,
        INTERACT_AT;

    }

}
