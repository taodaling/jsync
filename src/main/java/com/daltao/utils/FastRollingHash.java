package com.daltao.utils;

public class FastRollingHash {
    FastHashData hd;
    long hash;
    int size;

    public FastRollingHash(FastHashData hd) {
        this.hd = hd;
    }

    public void clear() {
        hash = 0;
        size = 0;
    }

    public long hash() {
        return hash;
    }

    public long hashV() {
        long ans = hash + hd.xp[size];
        return ans;
    }

    public void addLast(int x) {
        hash = hash + x * hd.xp[size];
        size++;
    }

    public void addFirst(int x) {
        hash = hash * hd.x + x;
        size++;
    }

    public void removeLast(int x) {
        hash = hash - x * hd.xp[size - 1];
        size--;
    }

    public void removeFirst(int x) {
        hash = (hash - x) * hd.invx;
        size--;
    }
}
