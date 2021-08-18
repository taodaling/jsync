package com.daltao.utils;

import java.security.MessageDigest;
import java.util.ArrayDeque;
import java.util.Deque;

public class FastRollingHashDeque implements HashDeque {
    Deque<Byte> dq;
    FastRollingHash rh;
    MessageDigest md;

    public FastRollingHashDeque(FastRollingHash rh, int n) {
        this.rh = rh;
        dq = new ArrayDeque<>(n + 10);
    }

    public void clear(){
        dq.clear();
        rh.clear();
    }

    public void addLast(byte x) {
        dq.addLast(x);
        rh.addLast(x);
    }

    public byte removeLast() {
        byte ans = dq.removeLast();
        rh.removeLast(ans);
        return ans;
    }

    public void addFirst(byte x) {
        dq.addFirst(x);
        rh.addFirst(x);
    }

    public byte removeFirst() {
        byte ans = dq.removeFirst();
        rh.removeFirst(ans);
        return ans;
    }

    public int size() {
        return dq.size();
    }

    public boolean isEmpty() {
        return dq.isEmpty();
    }

    public long hash() {
        return rh.hash();
    }

    public long hashV() {
        return rh.hashV();
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
