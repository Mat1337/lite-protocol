package me.mat.lite.protocol.connection.packet.packets;

import me.mat.lite.protocol.connection.packet.LitePacket;

public class CFlyingPacket extends LitePacket {

    public double x;
    public double y;
    public double z;
    public float yaw;
    public float pitch;
    public boolean onGround;
    public boolean hasPos;
    public boolean hasLook;

    public CFlyingPacket() {
        super(boolean.class);
    }

    public CFlyingPacket(Class<?>... types) {
        super(types);
    }

    @Override
    public void process(Object... values) {
        onGround = (boolean) values[0];
    }

    @Override
    public String toString() {
        return "FlyingPacket{" +
                "x=" + x +
                ", y=" + y +
                ", z=" + z +
                ", yaw=" + yaw +
                ", pitch=" + pitch +
                ", onGround=" + onGround +
                ", hasPos=" + hasPos +
                ", hasLook=" + hasLook +
                '}';
    }
}
