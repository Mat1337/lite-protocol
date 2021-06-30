package me.mat.lite.protocol.util.accessor;

import lombok.AllArgsConstructor;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.function.Predicate;
import java.util.stream.Stream;

/**
 * Created for pluto
 *
 * @author mat
 * @since 4/10/2021
 */

@AllArgsConstructor
public class Accessor {

    private final Class<?> aClass;

    public Constructor<?> getConstructor(Class<?>... params) {
        try {
            return aClass.getConstructor(params);
        } catch (NoSuchMethodException e) {
            return null;
        }
    }

    protected Method getMethod(Predicate<? super Method> predicate) {
        Method method = Stream.of(aClass.getDeclaredMethods()).filter(predicate).findFirst().orElse(null);
        if (method == null) {
            return null;
        }

        if (!method.isAccessible()) {
            method.setAccessible(true);
        }

        return method;
    }

    protected Method getMethod(int index) {
        Method[] methods = aClass.getDeclaredMethods();

        Method method = methods[Math.max(0, Math.min(methods.length - 1, index))];
        if (method == null) {
            return null;
        }

        if (!method.isAccessible()) {
            method.setAccessible(true);
        }

        return method;
    }

    protected Method getMethod(String name, Class<?>... params) {
        try {
            Method method = aClass.getDeclaredMethod(name, params);
            if (!method.isAccessible()) {
                method.setAccessible(true);
            }

            return method;
        } catch (NoSuchMethodException e) {
            return null;
        }
    }

    protected Field getField(Predicate<? super Field> predicate) {
        Field field = Stream.of(aClass.getDeclaredFields()).filter(predicate).findFirst().orElse(null);
        if (field == null) {
            return null;
        }

        if (!field.isAccessible()) {
            field.setAccessible(true);
        }

        return field;
    }

    protected Field getField(int index) {
        Field[] fields = aClass.getDeclaredFields();

        Field field = fields[Math.max(0, Math.min(fields.length - 1, index))];
        if (field == null) {
            return null;
        }

        if (!field.isAccessible()) {
            field.setAccessible(true);
        }

        return field;
    }

    protected Field getField(String name) {
        try {
            Field field = aClass.getDeclaredField(name);
            if (!field.isAccessible()) {
                field.setAccessible(true);
            }

            return field;
        } catch (NoSuchFieldException e) {
            return null;
        }
    }

}