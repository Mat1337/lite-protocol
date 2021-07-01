package me.mat.lite.protocol.connection.encoder;

import io.netty.channel.Channel;
import io.netty.handler.codec.MessageToByteEncoder;


public abstract class LitePacketEncoder<T> extends MessageToByteEncoder<T> {

    protected final Channel channel;

    protected final Object direction;

    public LitePacketEncoder(Channel channel, Object direction) {
        this.channel = channel;
        this.direction = direction;
    }

}
