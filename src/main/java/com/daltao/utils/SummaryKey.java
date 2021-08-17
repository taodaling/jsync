package com.daltao.utils;

import java.util.Arrays;

public class SummaryKey {
    long weakHash;
    byte[] data;

    public SummaryKey(long weakHash, byte[] data) {
        this.weakHash = weakHash;
        this.data = data;
    }

    @Override
    public int hashCode() {
        return Long.hashCode(weakHash) * 31 + Arrays.hashCode(data);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof SummaryKey) {
            return weakHash == ((SummaryKey) obj).weakHash && Arrays.equals(data, ((SummaryKey) obj).data);
        }
        return false;
    }
}
