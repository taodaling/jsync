package com.daltao.utils;

import java.io.*;

public class FileUtils {
    public static void iterateOverFile(File file, byte[] buffer, ByteConsumer consumer) throws IOException {
        try (InputStream is = new FileInputStream(file)) {
            int n;
            while ((n = is.read(buffer)) >= 0) {
                for (int i = 0; i < n; i++) {
                    consumer.consume(buffer[i]);
                }
            }
        }
    }
}
