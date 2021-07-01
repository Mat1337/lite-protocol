package me.mat.lite.protocol.connection.packet.packets;

import me.mat.lite.protocol.connection.packet.LitePacket;

public class SteerVehiclePacket extends LitePacket {

    public float strafeSpeed;
    public float forwardSpeed;
    public boolean jumping;
    public boolean sneaking;

    public SteerVehiclePacket() {
        super(float.class, float.class, byte.class);
    }

    @Override
    public void process(Object... values) {
        strafeSpeed = (float) values[0];
        forwardSpeed = (float) values[1];

        byte flag = (byte) values[2];
        jumping = (flag & 1) > 0;
        sneaking = (flag & 2) > 0;
    }

}