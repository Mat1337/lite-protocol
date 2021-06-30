package me.mat.lite.protocol.util;

import org.bukkit.Bukkit;

import java.util.HashMap;
import java.util.Map;

public class ReflectionUtil {

    private static final Map<String, Class<?>> CACHE
            = new HashMap<>();

    private static final String OBC_PREFIX
            = Bukkit.getServer().getClass().getPackage().getName();

    private static final String NMS_PREFIX
            = OBC_PREFIX.replace("org.bukkit.craftbukkit", "net.minecraft.server");

    public static final String VERSION
            = OBC_PREFIX.replace("org.bukkit.craftbukkit", "").replace(".", "");

    private ReflectionUtil() {
    }

    public static Class<?> getBukkitClass(String cls) {
        return getClass(OBC_PREFIX + "." + cls);
    }

    public static Class<?> getBukkitInventoryClass(String cls) {
        return getClass(OBC_PREFIX + ".inventory." + cls);
    }

    public static Class<?> getBukkitEntityClass(String cls) {
        return getClass(OBC_PREFIX + ".entity." + cls);
    }

    public static Class<?> getMinecraftClass(String cls) {
        return getClass(NMS_PREFIX + "." + cls);
    }

    public static Class<?> getClass(String cls) {
        // if the class cache contains the class name
        if (CACHE.containsKey(cls)) {

            // return the class from the cache
            return CACHE.get(cls);
        }


        try {
            // attempt to find the class
            Class<?> clazz = Class.forName(cls);

            // cache the class
            CACHE.put(cls, clazz);

            // return the class
            return clazz;
        } catch (ClassNotFoundException e) {
            throw new IllegalArgumentException("Cannot find " + cls, e);
        }
    }

}
