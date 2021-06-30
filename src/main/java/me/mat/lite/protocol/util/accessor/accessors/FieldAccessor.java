package me.mat.lite.protocol.util.accessor.accessors;

import me.mat.lite.protocol.util.accessor.Accessor;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Stream;

/**
 * Created for pluto
 *
 * @author mat
 * @since 4/10/2021
 */
public class FieldAccessor extends Accessor {

    private final Field field;

    public FieldAccessor(Class<?> aClass, String name) {
        super(aClass);

        this.field = getField(name);
    }

    public FieldAccessor(Class<?> aClass, int index) {
        super(aClass);

        this.field = getField(index);
    }

    public FieldAccessor(Class<?> aClass, Predicate<? super Field> predicate) {
        super(aClass);

        this.field = getField(predicate);
    }


    public FieldAccessor(Class<?> aClass, Class<?> type, int index) {
        super(aClass);

        List<Field> fields = new ArrayList<>();

        Stream.of(aClass.getDeclaredFields()).filter(f -> f.getType().equals(type)).forEach(fields::add);

        if (!fields.isEmpty()) {
            if (index > fields.size() - 1) {
                this.field = null;
            } else {
                this.field = fields.get(Math.max(0, index));

                if (!this.field.isAccessible()) {
                    this.field.setAccessible(true);
                }
            }
        } else {
            this.field = null;
        }
    }

    public <T> T get(Object handle) {
        try {
            return (T) field.get(handle);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }

        return null;
    }

    public void set(Object handle, Object value) {
        try {
            field.set(handle, value);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    public boolean isValid() {
        return field != null;
    }

}