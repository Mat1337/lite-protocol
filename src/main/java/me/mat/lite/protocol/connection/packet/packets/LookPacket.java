package me.mat.lite.protocol.connection.packet.packets;

public class LookPacket extends FlyingPacket {

    public LookPacket() {
        super(float.class, float.class, boolean.class);
        hasLook = true;
    }

    @Override
    public void process(Object... values) {
        yaw = (float) values[0];
        pitch = (float) values[1];
        onGround = (boolean) values[2];
    }

}
