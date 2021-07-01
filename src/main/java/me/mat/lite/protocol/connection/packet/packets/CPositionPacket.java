package me.mat.lite.protocol.connection.packet.packets;

public class CPositionPacket extends CFlyingPacket {

    public CPositionPacket() {
        super(double.class, double.class, double.class, boolean.class);
        hasPos = true;
    }

    @Override
    public void process(Object... values) {
        x = (double) values[0];
        y = (double) values[1];
        z = (double) values[2];

        onGround = (boolean) values[3];
    }

}
