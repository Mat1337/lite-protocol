package me.mat.lite.protocol.util;

import com.google.common.base.Predicate;
import com.google.common.collect.Iterators;
import com.google.common.collect.Maps;

import javax.annotation.Nullable;
import java.util.Iterator;
import java.util.Map;
import java.util.Random;

public enum Direction {

    DOWN(0, 1, -1, "down", Direction.EnumAxisDirection.NEGATIVE, Direction.EnumAxis.Y, new BlockPos(0, -1, 0)),
    UP(1, 0, -1, "up", Direction.EnumAxisDirection.POSITIVE, Direction.EnumAxis.Y, new BlockPos(0, 1, 0)),
    NORTH(2, 3, 2, "north", Direction.EnumAxisDirection.NEGATIVE, Direction.EnumAxis.Z, new BlockPos(0, 0, -1)),
    SOUTH(3, 2, 0, "south", Direction.EnumAxisDirection.POSITIVE, Direction.EnumAxis.Z, new BlockPos(0, 0, 1)),
    WEST(4, 5, 1, "west", Direction.EnumAxisDirection.NEGATIVE, Direction.EnumAxis.X, new BlockPos(-1, 0, 0)),
    EAST(5, 4, 3, "east", Direction.EnumAxisDirection.POSITIVE, Direction.EnumAxis.X, new BlockPos(1, 0, 0));

    private final int g;
    private final int h;
    private final int i;
    private final String j;
    private final Direction.EnumAxis k;
    private final Direction.EnumAxisDirection l;
    private final BlockPos m;
    private static final Direction[] n = new Direction[6];
    private static final Direction[] o = new Direction[4];
    private static final Map<String, Direction> p = Maps.newHashMap();

    Direction(int var3, int var4, int var5, String var6, Direction.EnumAxisDirection var7, Direction.EnumAxis var8, BlockPos var9) {
        this.g = var3;
        this.i = var5;
        this.h = var4;
        this.j = var6;
        this.k = var8;
        this.l = var7;
        this.m = var9;
    }

    public int a() {
        return this.g;
    }

    public int b() {
        return this.i;
    }

    public Direction.EnumAxisDirection c() {
        return this.l;
    }

    public Direction opposite() {
        return fromType1(this.h);
    }

    public Direction e() {
        switch (this) {
            case NORTH:
                return EAST;
            case EAST:
                return SOUTH;
            case SOUTH:
                return WEST;
            case WEST:
                return NORTH;
            default:
                throw new IllegalStateException("Unable to get Y-rotated facing of " + this);
        }
    }

    public Direction f() {
        switch (this) {
            case NORTH:
                return WEST;
            case EAST:
                return NORTH;
            case SOUTH:
                return EAST;
            case WEST:
                return SOUTH;
            default:
                throw new IllegalStateException("Unable to get CCW facing of " + this);
        }
    }

    public int getAdjacentX() {
        return this.k == Direction.EnumAxis.X ? this.l.a() : 0;
    }

    public int getAdjacentY() {
        return this.k == Direction.EnumAxis.Y ? this.l.a() : 0;
    }

    public int getAdjacentZ() {
        return this.k == Direction.EnumAxis.Z ? this.l.a() : 0;
    }

    public String j() {
        return this.j;
    }

    public Direction.EnumAxis k() {
        return this.k;
    }

    public static Direction fromType1(int var0) {
        return n[MathHelper.a(var0 % n.length)];
    }

    public static Direction fromType2(int var0) {
        return o[MathHelper.a(var0 % o.length)];
    }

    public static Direction fromAngle(double var0) {
        return fromType2(MathHelper.floor(var0 / 90.0D + 0.5D) & 3);
    }

    public static Direction a(Random var0) {
        return values()[var0.nextInt(values().length)];
    }

    public String toString() {
        return this.j;
    }

    public String getName() {
        return this.j;
    }

    public static Direction a(Direction.EnumAxisDirection var0, Direction.EnumAxis var1) {
        Direction[] var2 = values();
        int var3 = var2.length;

        for (int var4 = 0; var4 < var3; ++var4) {
            Direction var5 = var2[var4];
            if (var5.c() == var0 && var5.k() == var1) {
                return var5;
            }
        }

        throw new IllegalArgumentException("No such direction: " + var0 + " " + var1);
    }

    static {
        Direction[] var0 = values();
        int var1 = var0.length;

        for (int var2 = 0; var2 < var1; ++var2) {
            Direction var3 = var0[var2];
            n[var3.g] = var3;
            if (var3.k().c()) {
                o[var3.i] = var3;
            }

            p.put(var3.j().toLowerCase(), var3);
        }

    }

    public enum EnumDirectionLimit implements Predicate<Direction>, Iterable<Direction> {
        HORIZONTAL,
        VERTICAL;

        EnumDirectionLimit() {
        }

        public Direction[] a() {
            switch (this) {
                case HORIZONTAL:
                    return new Direction[]{Direction.NORTH, Direction.EAST, Direction.SOUTH, Direction.WEST};
                case VERTICAL:
                    return new Direction[]{Direction.UP, Direction.DOWN};
                default:
                    throw new Error("Someone's been tampering with the universe!");
            }
        }

        public Direction a(Random var1) {
            Direction[] var2 = this.a();
            return var2[var1.nextInt(var2.length)];
        }

        public boolean a(Direction var1) {
            return var1 != null && var1.k().d() == this;
        }

        public Iterator<Direction> iterator() {
            return Iterators.forArray(this.a());
        }

        @Override
        public boolean apply(@Nullable Direction direction) {
            return false;
        }
    }

    public enum EnumAxisDirection {
        POSITIVE(1, "Towards positive"),
        NEGATIVE(-1, "Towards negative");

        private final int c;
        private final String d;

        EnumAxisDirection(int var3, String var4) {
            this.c = var3;
            this.d = var4;
        }

        public int a() {
            return this.c;
        }

        public String toString() {
            return this.d;
        }
    }

    public enum EnumAxis implements Predicate<Direction> {
        X("x", Direction.EnumDirectionLimit.HORIZONTAL),
        Y("y", Direction.EnumDirectionLimit.VERTICAL),
        Z("z", Direction.EnumDirectionLimit.HORIZONTAL);

        private static final Map<String, Direction.EnumAxis> d = Maps.newHashMap();
        private final String e;
        private final Direction.EnumDirectionLimit f;

        private EnumAxis(String var3, Direction.EnumDirectionLimit var4) {
            this.e = var3;
            this.f = var4;
        }

        public String a() {
            return this.e;
        }

        public boolean b() {
            return this.f == Direction.EnumDirectionLimit.VERTICAL;
        }

        public boolean c() {
            return this.f == Direction.EnumDirectionLimit.HORIZONTAL;
        }

        public String toString() {
            return this.e;
        }

        public boolean a(Direction var1) {
            return var1 != null && var1.k() == this;
        }

        public Direction.EnumDirectionLimit d() {
            return this.f;
        }

        public String getName() {
            return this.e;
        }

        static {
            Direction.EnumAxis[] var0 = values();
            int var1 = var0.length;

            for (int var2 = 0; var2 < var1; ++var2) {
                Direction.EnumAxis var3 = var0[var2];
                d.put(var3.a().toLowerCase(), var3);
            }

        }

        @Override
        public boolean apply(@Nullable Direction direction) {
            return false;
        }
    }
}
