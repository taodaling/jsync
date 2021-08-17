package com.daltao.utils;

import java.io.*;
import java.util.LinkedHashMap;
import java.util.Map;

public class FileCache extends LinkedHashMap<File, RandomAccessFile> {
    int threshold;

    public FileCache(int threshold) {
        this.threshold = threshold;
    }

    @Override
    protected boolean removeEldestEntry(Map.Entry<File, RandomAccessFile> eldest) {
        if (size() <= threshold) {
            return false;
        }
        try {
            eldest.getValue().close();
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
        return true;
    }

    @Override
    public void clear() {
        try {
            for (RandomAccessFile fis : values()) {
                fis.close();
            }
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
        super.clear();
    }
}
