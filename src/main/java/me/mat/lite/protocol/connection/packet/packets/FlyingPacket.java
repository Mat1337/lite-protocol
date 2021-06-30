package me.mat.lite.protocol.connection.packet.packets;

import me.mat.lite.protocol.connection.packet.LitePacket;

public class FlyingPacket extends LitePacket {

    public double x;
    public double y;
    public double z;
    public float yaw;
    public float pitch;
    public boolean onGround;
    public boolean hasPos;
    public boolean hasLook;

    public FlyingPacket() {
        super(boolean.class);
    }

    public FlyingPacket(Class<?>... types) {
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
