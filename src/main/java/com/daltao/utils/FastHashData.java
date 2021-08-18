package com.daltao.utils;

import java.math.BigInteger;

public class FastHashData {
    long x;
    long invx;
    long[] xp;
    long[] invxp;

    public FastHashData(long x, int n){
        this.x = x;
        invx = BigInteger.valueOf(x).modInverse(BigInteger.valueOf(1).shiftLeft(64).negate()).longValue();
        xp = new long[n + 10];
        invxp = new long[n + 10];
        xp[0] = 1;
        invxp[0] = 1;
        for(int i = 1; i < xp.length; i++){
            xp[i] = xp[i - 1] * x;
            invxp[i] = invxp[i - 1] * invx;
        }
    }
}
