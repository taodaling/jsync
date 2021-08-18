package com.daltao.utils;

import java.math.BigInteger;
import java.util.BitSet;

public class HashData {
    int x;
    int invx;
    int mod;
    int[] xp;
    int[] invxp;

    public HashData(int x, int mod, int n){
        this.x = x;
        this.mod = mod;
        invx = BigInteger.valueOf(x).modInverse(BigInteger.valueOf(mod)).intValue();
        xp = new int[n + 10];
        invxp = new int[n + 10];
        xp[0] = 1;
        invxp[0] = 1;
        for(int i = 1; i < xp.length; i++){
            xp[i] = (int) ((long)xp[i - 1] * x % mod);
            invxp[i] = (int) ((long)invxp[i - 1] * invx % mod);
        }
    }
}
