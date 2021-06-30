package me.mat.lite.protocol.connection;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler;
import io.netty.handler.codec.ByteToMessageDecoder;
import lombok.Getter;
import me.mat.lite.protocol.connection.decoder.LitePacketDecoder;
import me.mat.lite.protocol.connection.decoder.decoders.LitePacketDecoder1_8;
import me.mat.lite.protocol.connection.packet.LitePacket;
import me.mat.lite.protocol.connection.packet.packets.*;
import me.mat.lite.protocol.util.ReflectionUtil;
import me.mat.lite.protocol.util.accessor.accessors.ConstructorAccessor;
import me.mat.lite.protocol.util.accessor.accessors.FieldAccessor;
import me.mat.lite.protocol.util.accessor.accessors.MethodAccessor;
import org.bukkit.entity.Player;

@Getter
public class PlayerConnection {

    private static final MethodAccessor GET_HANDLE = new MethodAccessor(
            ReflectionUtil.getBukkitEntityClass("CraftPlayer"),
            "getHandle"
    );

    private static final FieldAccessor PLAYER_CONNECTION = new FieldAccessor(
            ReflectionUtil.getMinecraftClass("EntityPlayer"),
            "playerConnection"
    );

    private static final FieldAccessor NETWORK_MANAGER
            = new FieldAccessor(
            ReflectionUtil.getMinecraftClass("PlayerConnection"),
            "networkManager"
    );

    private static final FieldAccessor CHANNEL = new FieldAccessor(
            ReflectionUtil.getMinecraftClass("NetworkManager"),
            "channel"
    );

    private static final FieldAccessor DIRECTION = new FieldAccessor(
            ReflectionUtil.getMinecraftClass("PacketDecoder"),
            ReflectionUtil.getMinecraftClass("EnumProtocolDirection"),
            0
    );

    private static final ConstructorAccessor PACKET_DECODER = new ConstructorAccessor(
            ReflectionUtil.getMinecraftClass("PacketDecoder"),
            ReflectionUtil.getMinecraftClass("EnumProtocolDirection")
    );

    private final ConnectionManager connectionManager;
    private final Player player;

    // used for keeping track of the decoders
    private LitePacketDecoder<?> liteDecoder;

    public PlayerConnection(ConnectionManager connectionManager, Player player) {
        this.connectionManager = connectionManager;
        this.player = player;

        // inject the channel handler
        this.load();
    }

    public void onPacketReceive(LitePacket packet) {
        if (packet instanceof KeepAlivePacket) {
            KeepAlivePacket keepAlivePacket = (KeepAlivePacket) packet;
            player.sendMessage("KeepAlive: " + keepAlivePacket.entityID);
        } else if (packet instanceof ChatPacket) {
            ChatPacket chatPacket = (ChatPacket) packet;
            player.sendMessage("Your message: " + chatPacket.message);
        } else if (packet instanceof UseEntityPacket) {
            UseEntityPacket useEntityPacket = (UseEntityPacket) packet;
            if (useEntityPacket.action == UseEntityPacket.Action.ATTACK) {
                player.sendMessage("id: " + useEntityPacket.entityID);
            } else if (useEntityPacket.action == UseEntityPacket.Action.INTERACT_AT) {
                player.sendMessage("look: " + useEntityPacket.look);
            }
        } else if (packet instanceof BlockDigPacket) {
            BlockDigPacket blockDigPacket = (BlockDigPacket) packet;
            if (blockDigPacket.type == BlockDigPacket.Type.RELEASE_USE_ITEM) {
                player.sendMessage("You have released your item");
            } else if (blockDigPacket.type == BlockDigPacket.Type.START_DESTROY_BLOCK) {
                player.sendMessage("x: " + blockDigPacket.blockPos.x + ", y: " + blockDigPacket.blockPos.y + ", z: " + blockDigPacket.blockPos.z);
            }
        } else if (packet instanceof BlockPlacePacket) {
            BlockPlacePacket blockPlacePacket = (BlockPlacePacket) packet;
            player.sendMessage("---");
            player.sendMessage(blockPlacePacket.stack.getType().toString());
            player.sendMessage(blockPlacePacket.blockPos.toString());
            player.sendMessage("x: " + blockPlacePacket.facingX + ", y: " + blockPlacePacket.facingY + " z: " + blockPlacePacket.facingZ);
            player.sendMessage("---");
        } else if (packet instanceof HeldItemSlotPacket) {
            HeldItemSlotPacket heldItemSlotPacket = (HeldItemSlotPacket) packet;
            player.sendMessage("Slot changed to: " + heldItemSlotPacket.slot);
        } else if (packet instanceof ArmAnimationPacket) {
            ArmAnimationPacket armAnimationPacket = (ArmAnimationPacket) packet;
            player.sendMessage("You have swung your arm: " + armAnimationPacket.timestamp);
        }
    }

    private void load() {
        // get the channel
        Channel channel = getChannel();

        // if the channel is invalid
        if (channel == null) {

            // return out of the method
            return;
        }

        // execute the inject method
        channel.eventLoop().execute(() -> inject(channel));
    }

    private void inject(Channel channel) {
        channel.eventLoop().execute(() -> {
            Object decoder = channel.pipeline().get("decoder");
            if (decoder != null) {
                if (decoder instanceof ByteToMessageDecoder) {
                    if (liteDecoder == null) {
                        Object direction = DIRECTION.get(decoder);
                        liteDecoder = new LitePacketDecoder1_8(this, direction);
                        if (direction != null) {
                            channel.pipeline().replace(
                                    "decoder",
                                    "decoder",
                                    liteDecoder
                            );
                        }
                    }
                }
            }
        });
    }

    public void close() {
        // get the channel
        Channel channel = getChannel();

        // if the channel is invalid
        if (channel == null) {

            // return out of the method
            return;
        }

        // create a vanilla packet decoder
        ChannelHandler packetDecoder
                = PACKET_DECODER.newInstance(liteDecoder.getDirection());

        // remove the handler from the channel
        channel.eventLoop().execute(() -> channel.pipeline().replace(
                "decoder",
                "decoder",
                packetDecoder
        ));

        // null the lite decoder
        liteDecoder = null;
    }

    private Channel getChannel() {
        return CHANNEL.get(getNetworkManager());
    }

    private Object getNetworkManager() {
        return NETWORK_MANAGER.get(getPlayerConnection());
    }

    private Object getPlayerConnection() {
        return PLAYER_CONNECTION.get(getEntityPlayer());
    }

    private Object getEntityPlayer() {
        return GET_HANDLE.invoke(player);
    }

}
