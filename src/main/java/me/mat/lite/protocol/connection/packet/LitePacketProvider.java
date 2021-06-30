package me.mat.lite.protocol.connection.packet;

import me.mat.lite.protocol.connection.packet.packets.*;

import java.util.HashMap;
import java.util.Map;

public class LitePacketProvider {

    public static final Map<Integer, Class<?>> CLIENT_PACKETS = new HashMap<>();

    private static int clientPacketID = 0;

    public static void load() {
        loadClientPackets(
                KeepAlivePacket.class,
                ChatPacket.class,
                UseEntityPacket.class,
                FlyingPacket.class,
                PositionPacket.class,
                LookPacket.class,
                PositionLookPacket.class,
                BlockDigPacket.class,
                BlockPlacePacket.class,
                HeldItemSlotPacket.class,
                ArmAnimationPacket.class
        );
    }

    private static void loadClientPackets(Class<?>... packetClasses) {
        for (Class<?> packetClass : packetClasses) {
            loadClientPacket(packetClass);
        }
    }

    private static void loadClientPacket(Class<?> packetCls) {
        CLIENT_PACKETS.put(clientPacketID++, packetCls);
    }

}
