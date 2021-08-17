package com.daltao.utils;

import java.security.MessageDigest;
import java.util.ArrayDeque;
import java.util.Deque;

public class RollingHashDeque {
    Deque<Byte> dq;
    RollingHash rh1;
    RollingHash rh2;
    MessageDigest md;

    public RollingHashDeque(RollingHash rh1, RollingHash rh2, int n) {
        this.rh1 = rh1;
        this.rh2 = rh2;
        dq = new ArrayDeque<>(n + 10);
    }

    public void clear(){
        dq.clear();
        rh1.clear();
        rh2.clear();
    }

    public void addLast(byte x) {
        dq.addLast(x);
        rh1.addLast(x);
        rh2.addLast(x);
    }

    public byte removeLast() {
        byte ans = dq.removeLast();
        rh1.removeLast(ans);
        rh2.removeLast(ans);
        return ans;
    }

    public void addFirst(byte x) {
        dq.addFirst(x);
        rh1.addFirst(x);
        rh2.addFirst(x);
    }

    public byte removeFirst() {
        byte ans = dq.removeFirst();
        rh1.removeFirst(ans);
        rh2.removeFirst(ans);
        return ans;
    }

    public int size() {
        return dq.size();
    }

    public boolean isEmpty() {
        return dq.isEmpty();
    }

    public long hash() {
        return IntegerUtils.mergeAsLong(rh1.hash(), rh2.hash());
    }

    public long hashV() {
        return IntegerUtils.mergeAsLong(rh1.hashV(), rh2.hashV());
    }

    public byte[] md5() {
        if (md == null) {
            md = MD5Utils.create();
        }
        md.reset();
        for (byte b : dq) {
            md.update(b);
        }
        return md.digest();
    }
}
