package me.mat.lite.protocol.connection.packet.packets.client;

public class CPositionLookPacket extends CFlyingPacket {

    public CPositionLookPacket() {
        super(double.class, double.class, double.class, float.class, float.class, boolean.class);
        hasPos = true;
        hasLook = true;
    }

    @Override
    public void process(Object... values) {
        x = (double) values[0];
        y = (double) values[1];
        z = (double) values[2];

        yaw = (float) values[3];
        pitch = (float) values[4];

        onGround = (boolean) values[5];
    }

}
