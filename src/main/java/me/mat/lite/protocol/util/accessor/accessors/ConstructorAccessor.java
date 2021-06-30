package me.mat.lite.protocol.util.accessor.accessors;

import me.mat.lite.protocol.util.accessor.Accessor;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

/**
 * Created for pluto
 *
 * @author mat
 * @since 4/12/2021
 */
public class ConstructorAccessor extends Accessor {

    private final Class<?>[] parameters;
    private final Constructor<?> constructor;

    public ConstructorAccessor(Class<?> aClass, Class<?>... parameters) {
        super(aClass);

        this.parameters = parameters;
        this.constructor = getConstructor(parameters);
    }

    public <T> T newInstance(Object... parameters) {
        if (this.parameters.length != parameters.length) {
            throw new IllegalArgumentException("Invalid number of arguments");
        }

        try {
            return (T) constructor.newInstance(parameters);
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
            return null;
        }
    }

    public boolean isValid() {
        return constructor != null;
    }

}