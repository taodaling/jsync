package com.daltao.utils;

public class IntegerUtils {
    public static long INT_TO_LONG_MASK = (1L << 32) - 1;

    public static long mergeAsLong(int a, int b) {
        return ((long) a << 32) | (b & INT_TO_LONG_MASK);
    }
}
