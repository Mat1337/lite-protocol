package me.mat.lite.protocol.connection.packet.protocol.providers;

import me.mat.lite.protocol.connection.packet.protocol.PacketProtocolProvider;
import net.minecraft.server.v1_8_R3.EnumProtocol;
import net.minecraft.server.v1_8_R3.EnumProtocolDirection;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

public class PacketProtocolProvider1_8 implements PacketProtocolProvider {

    // maximum packets that this provider will fetch
    private static final int MAX_PACKETS = 250;

    // fetch all the enum protocol values
    private static final EnumProtocol[] PROTOCOLS = EnumProtocol.values();

    @Override
    public Object[] listPackets(String protocol, String direction) {
        // get the protocol
        EnumProtocol enumProtocol = EnumProtocol.valueOf(protocol);

        // get the protocol direction
        EnumProtocolDirection protocolDirection = EnumProtocolDirection.valueOf(direction);

        // define a list that will hold all the packets
        List<Object> packets = new ArrayList<>();

        // loop through the max packets count
        for (int i = 0; i < MAX_PACKETS; i++) {
            try {
                // get the packet
                Object packet = enumProtocol.a(protocolDirection, i);

                // if the packet is valid
                if (packet != null) {

                    // add the packet to the list
                    packets.add(packet);
                }
            } catch (Exception ignored) {
            }
        }

        // return the array of the packets
        return packets.toArray();
    }

    @Override
    public String[] listProtocols() {
        return Stream.of(PROTOCOLS).map(Enum::toString).toArray(String[]::new);
    }

}
