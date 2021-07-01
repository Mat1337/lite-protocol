package me.mat.lite.protocol.connection.packet;

import me.mat.lite.protocol.connection.packet.packets.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

public class LitePacketProvider {

    private static final Map<String, Map<String, List<Class<?>>>> PROTOCOL_MAP = new HashMap<>();

    public static void load() {
        Stream.of(Protocol.values()).forEach(protocol -> PROTOCOL_MAP.put(protocol.toString(), new HashMap<>()));

        loadHandShaking(Protocol.HANDSHAKING);
        loadPlay(Protocol.PLAY);
        loadStatus(Protocol.STATUS);
        loadLogin(Protocol.LOGIN);
    }

    public static Class<?> getPacket(String protocol, String direction, int id) {
        if (!PROTOCOL_MAP.containsKey(protocol)) {
            return null;
        }

        Map<String, List<Class<?>>> packetMap = PROTOCOL_MAP.get(protocol);
        if (!packetMap.containsKey(direction)) {
            return null;
        }

        List<Class<?>> classes = packetMap.get(direction);
        if (classes.size() < id) {
            return null;
        }

        return classes.get(id);
    }

    private static void loadHandShaking(Protocol protocol) {
        addPacket(protocol, ProtocolDirection.SERVERBOUND, CHandshakePacket.class);
    }

    private static void loadPlay(Protocol protocol) {
        addPacket(protocol, ProtocolDirection.SERVERBOUND, CKeepAlivePacket.class);
        addPacket(protocol, ProtocolDirection.SERVERBOUND, CChatPacket.class);
        addPacket(protocol, ProtocolDirection.SERVERBOUND, CUseEntityPacket.class);
        addPacket(protocol, ProtocolDirection.SERVERBOUND, CFlyingPacket.class);
        addPacket(protocol, ProtocolDirection.SERVERBOUND, CPositionPacket.class);
        addPacket(protocol, ProtocolDirection.SERVERBOUND, CLookPacket.class);
        addPacket(protocol, ProtocolDirection.SERVERBOUND, CPositionLookPacket.class);
        addPacket(protocol, ProtocolDirection.SERVERBOUND, CBlockDigPacket.class);
        addPacket(protocol, ProtocolDirection.SERVERBOUND, CBlockPlacePacket.class);
        addPacket(protocol, ProtocolDirection.SERVERBOUND, CHeldItemSlotPacket.class);
        addPacket(protocol, ProtocolDirection.SERVERBOUND, CArmAnimationPacket.class);
        addPacket(protocol, ProtocolDirection.SERVERBOUND, CEntityActionPacket.class);
        addPacket(protocol, ProtocolDirection.SERVERBOUND, CSteerVehiclePacket.class);
        addPacket(protocol, ProtocolDirection.SERVERBOUND, CCloseWindowPacket.class);
        addPacket(protocol, ProtocolDirection.SERVERBOUND, CWindowClickPacket.class);
        addPacket(protocol, ProtocolDirection.SERVERBOUND, CTransactionPacket.class);
        addPacket(protocol, ProtocolDirection.SERVERBOUND, CCreativeSlotPacket.class);
        addPacket(protocol, ProtocolDirection.SERVERBOUND, CEnchantItemPacket.class);
    }

    private static void loadStatus(Protocol protocol) {
        addPacket(protocol, ProtocolDirection.SERVERBOUND, CStartPacket.class);
        addPacket(protocol, ProtocolDirection.SERVERBOUND, CPingPacket.class);
    }

    private static void loadLogin(Protocol protocol) {
        addPacket(protocol, ProtocolDirection.SERVERBOUND, CLoginStartPacket.class);
        addPacket(protocol, ProtocolDirection.SERVERBOUND, CLoginEncryptionBeginPacket.class);
    }

    private static void addPacket(Protocol protocol, ProtocolDirection direction, Class<?> packet) {
        if (!PROTOCOL_MAP.containsKey(protocol.toString())) {
            return;
        }
        Map<String, List<Class<?>>> packetMap = PROTOCOL_MAP.get(protocol.toString());
        if (!packetMap.containsKey(direction.toString())) {
            packetMap.put(direction.toString(), new ArrayList<>());
        }
        packetMap.get(direction.toString()).add(packet);
        PROTOCOL_MAP.put(protocol.toString(), packetMap);
    }

    public enum Protocol {

        HANDSHAKING,
        PLAY,
        STATUS,
        LOGIN

    }

    public enum ProtocolDirection {

        SERVERBOUND,
        CLIENTBOUND;

    }

}
