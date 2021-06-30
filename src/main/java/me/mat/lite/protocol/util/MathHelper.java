package me.mat.lite.protocol.util;

public class MathHelper {

    public static int a(int var0) {
        return var0 >= 0 ? var0 : -var0;
    }

    public static int floor(double var0) {
        int var2 = (int) var0;
        return var0 < (double) var2 ? var2 - 1 : var2;
    }


}
