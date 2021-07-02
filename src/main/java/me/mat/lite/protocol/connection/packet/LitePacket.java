package me.mat.lite.protocol.connection.packet;

import lombok.Getter;

import java.util.Map;

@Getter
public abstract class LitePacket {

    protected final Class<?>[] types;

    public LitePacket(Class<?>... types) {
        this.types = types;
    }

    public void process(Object... values) {
    }

    public void process(Map<Object, Class<?>> data) {
    }

    public int getStringLength() {
        return 100;
    }

    public static Class<?>[] merge(Class<?>[] first, Class<?>... classes) {
        Class<?>[] array = new Class<?>[first.length + classes.length];

        System.arraycopy(classes, 0, array, 0, classes.length);
        System.arraycopy(first, 0, array, classes.length, first.length);

        return array;
    }

}
