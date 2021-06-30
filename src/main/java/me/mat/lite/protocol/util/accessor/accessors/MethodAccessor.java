package me.mat.lite.protocol.util.accessor.accessors;

import me.mat.lite.protocol.util.accessor.Accessor;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.function.Predicate;

/**
 * Created for pluto
 *
 * @author mat
 * @since 4/10/2021
 */
public class MethodAccessor extends Accessor {

    private final Method method;

    public MethodAccessor(Class<?> aClass, String name, Class<?>... params) {
        super(aClass);

        this.method = getMethod(name, params);
    }

    public MethodAccessor(Class<?> aClass, int index) {
        super(aClass);

        this.method = getMethod(index);
    }

    public MethodAccessor(Class<?> aClass, Predicate<? super Method> predicate) {
        super(aClass);

        this.method = getMethod(predicate);
    }

    public <T> T invoke(Object handle, Object... params) {
        try {
            return (T) method.invoke(handle, params);
        } catch (IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }

        return null;
    }

    public boolean isValid() {
        return method != null;
    }

}