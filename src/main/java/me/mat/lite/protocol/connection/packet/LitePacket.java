package me.mat.lite.protocol.connection.packet;

import lombok.Getter;

@Getter
public abstract class LitePacket {

    protected final Class<?>[] types;

    public LitePacket(Class<?>... types) {
        this.types = types;
    }

    public abstract void process(Object... values);

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
