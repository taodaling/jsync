package com.daltao.client;

import com.daltao.dto.Directory;

import java.io.File;
import java.util.List;

public class CreateNecessaryDirectory implements Runnable {
    List<Directory> dirs;
    File[] files;
    File rootFile;

    public CreateNecessaryDirectory(List<Directory> dirs, File rootFile) {
        this.dirs = dirs;
        this.rootFile = rootFile;
    }

    @Override
    public void run() {
        files = new File[dirs.size()];
        rootFile.mkdirs();
        files[0] = rootFile;
        for (int i = 1; i < dirs.size(); i++) {
            Directory d = dirs.get(i);
            File file = new File(files[d.parentId], d.name);
            file.mkdir();
            files[i] = file;
        }
    }
}
