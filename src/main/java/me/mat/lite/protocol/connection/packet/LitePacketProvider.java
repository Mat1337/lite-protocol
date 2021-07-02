package me.mat.lite.protocol.connection.packet;

import me.mat.lite.protocol.connection.packet.packets.*;
import me.mat.lite.protocol.connection.packet.packets.server.SKeepAlivePacket;
import me.mat.lite.protocol.connection.packet.protocol.PacketProtocolProvider;
import me.mat.lite.protocol.connection.packet.protocol.providers.PacketProtocolProvider1_8;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

public class LitePacketProvider {

    // used for storing all the protocol packets
    private static final Map<String, Map<String, List<Class<?>>>> PROTOCOL_MAP = new HashMap<>();

    // used for retrieving lite packets from NMS packets
    public static final Map<Class<?>, Class<?>> PACKET_LOOKUP = new HashMap<>();

    // used for retrieving NMS packets from lite packets
    public static final Map<Class<?>, Class<?>> REVERSE_PACKET_LOOKUP = new HashMap<>();

    public static void load() {
        // load the protocol map that will hold all the packets
        Stream.of(Protocol.values()).forEach(protocol -> PROTOCOL_MAP.put(protocol.toString(), new HashMap<>()));

        // load all the packets
        loadHandShaking();
        loadPlay();
        loadStatus();
        loadLogin();

        // load all the nms packets
        loadNMSPackets(new PacketProtocolProvider1_8());
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
        if (classes.size() <= id) {
            return null;
        }

        return classes.get(id);
    }

    private static void loadNMSPackets(PacketProtocolProvider protocolProvider) {
        // list all the protocols
        String[] protocols = protocolProvider.listProtocols();

        // loop through all the protocols
        for (String protocol : protocols) {

            // loop through all the protocol directions
            for (ProtocolDirection direction : ProtocolDirection.values()) {

                // get all the packets for the protocol
                Object[] packets = protocolProvider.listPackets(protocol, direction.toString());

                // loop through all the packets
                for (int i = 0, packetsLength = packets.length; i < packetsLength; i++) {
                    // get the current packet
                    Object packet = packets[i];

                    // get the lite packet
                    Class<?> litePacket = getPacket(protocol, direction.toString(), i);

                    // if the lite packet is valid
                    if (litePacket != null) {

                        // cache the packets to the lookup table
                        PACKET_LOOKUP.put(packet.getClass(), litePacket);

                        // cache the packets to the reverse lookup table
                        REVERSE_PACKET_LOOKUP.put(litePacket, packet.getClass());
                    }
                }
            }
        }
    }

    private static void loadHandShaking() {
        addHandShakePacket(ProtocolDirection.SERVERBOUND, CHandshakePacket.class);
    }

    private static void loadPlay() {
        addPlayPacket(ProtocolDirection.CLIENTBOUND, SKeepAlivePacket.class);
        addPlayPacket(ProtocolDirection.SERVERBOUND, CKeepAlivePacket.class);
        addPlayPacket(ProtocolDirection.SERVERBOUND, CChatPacket.class);
        addPlayPacket(ProtocolDirection.SERVERBOUND, CUseEntityPacket.class);
        addPlayPacket(ProtocolDirection.SERVERBOUND, CFlyingPacket.class);
        addPlayPacket(ProtocolDirection.SERVERBOUND, CPositionPacket.class);
        addPlayPacket(ProtocolDirection.SERVERBOUND, CLookPacket.class);
        addPlayPacket(ProtocolDirection.SERVERBOUND, CPositionLookPacket.class);
        addPlayPacket(ProtocolDirection.SERVERBOUND, CBlockDigPacket.class);
        addPlayPacket(ProtocolDirection.SERVERBOUND, CBlockPlacePacket.class);
        addPlayPacket(ProtocolDirection.SERVERBOUND, CHeldItemSlotPacket.class);
        addPlayPacket(ProtocolDirection.SERVERBOUND, CArmAnimationPacket.class);
        addPlayPacket(ProtocolDirection.SERVERBOUND, CEntityActionPacket.class);
        addPlayPacket(ProtocolDirection.SERVERBOUND, CSteerVehiclePacket.class);
        addPlayPacket(ProtocolDirection.SERVERBOUND, CCloseWindowPacket.class);
        addPlayPacket(ProtocolDirection.SERVERBOUND, CWindowClickPacket.class);
        addPlayPacket(ProtocolDirection.SERVERBOUND, CTransactionPacket.class);
        addPlayPacket(ProtocolDirection.SERVERBOUND, CCreativeSlotPacket.class);
        addPlayPacket(ProtocolDirection.SERVERBOUND, CEnchantItemPacket.class);
    }

    private static void loadStatus() {
        addStatusPacket(ProtocolDirection.SERVERBOUND, CStartPacket.class);
        addStatusPacket(ProtocolDirection.SERVERBOUND, CPingPacket.class);
    }

    private static void loadLogin() {
        addLoginPacket(ProtocolDirection.SERVERBOUND, CLoginStartPacket.class);
        addLoginPacket(ProtocolDirection.SERVERBOUND, CLoginEncryptionBeginPacket.class);
    }

    private static void addHandShakePacket(ProtocolDirection direction, Class<?> packet) {
        addPacket(Protocol.HANDSHAKING, direction, packet);
    }

    private static void addPlayPacket(ProtocolDirection direction, Class<?> packet) {
        addPacket(Protocol.PLAY, direction, packet);
    }

    private static void addStatusPacket(ProtocolDirection direction, Class<?> packet) {
        addPacket(Protocol.STATUS, direction, packet);
    }

    private static void addLoginPacket(ProtocolDirection direction, Class<?> packet) {
        addPacket(Protocol.LOGIN, direction, packet);
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
