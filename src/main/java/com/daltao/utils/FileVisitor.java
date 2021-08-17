package com.daltao.utils;

import java.io.File;

public interface FileVisitor {
    public void enterDirectory(File dir) throws Exception;

    public void leaveDirectory(File dir) throws Exception;

    public void visitFile(File file) throws Exception;
}
