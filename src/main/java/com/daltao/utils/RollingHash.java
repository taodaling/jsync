package com.daltao.utils;

public class RollingHash {
    HashData hd;
    int hash;
    int size;

    public RollingHash(HashData hd) {
        this.hd = hd;
    }

    public void clear() {
        hash = 0;
        size = 0;
    }

    public int hash() {
        return hash;
    }

    public int hashV() {
        int ans = hash + hd.xp[size];
        if (ans >= hd.mod) {
            ans -= hd.mod;
        }
        return ans;
    }

    public void addLast(int x) {
        hash = (int) ((hash + (long) x * hd.xp[size]) % hd.mod);
        size++;
    }

    public void addFirst(int x) {
        hash = (int) (((long) hash * hd.x + x) % hd.mod);
        size++;
    }

    public void removeLast(int x) {
        hash = (int) ((hash - (long) x * hd.xp[size - 1]) % hd.mod);
        if (hash < 0) {
            hash += hd.mod;
        }
        size--;
    }

    public void removeFirst(int x) {
        hash = (int)((long)(hash - x) * hd.invx % hd.mod);
        if(hash < 0){
            hash += hd.mod;
        }
        size--;
    }
}
