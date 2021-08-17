package com.daltao.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;

public class FileUtils {
    public static void iterateOverFile(File file, byte[] buffer, ByteConsumer consumer) throws Exception {
        try (InputStream is = new FileInputStream(file)) {
            int n;
            while ((n = is.read(buffer)) >= 0) {
                for (int i = 0; i < n; i++) {
                    consumer.consume(buffer[i]);
                }
            }
        }
    }

    public static void visitFileTree(File root, FileVisitor visitor) throws Exception {
        if (root.isFile()) {
            visitor.visitFile(root);
            return;
        }
        visitor.enterDirectory(root);
        for (File node : root.listFiles()) {
            visitFileTree(node, visitor);
        }
        visitor.leaveDirectory(root);
    }

    public static boolean under(File root, File node) {
        while (node != null) {
            if (root.equals(node)) {
                return true;
            }
            node = node.getParentFile();
        }
        return false;
    }

    public static void deepDelete(File root) throws Exception {
        visitFileTree(root, new FileVisitor() {
            @Override
            public void enterDirectory(File dir) throws Exception {

            }

            @Override
            public void leaveDirectory(File dir) throws Exception {
                dir.delete();
            }

            @Override
            public void visitFile(File file) throws Exception {
                file.delete();
            }
        });
    }
}
