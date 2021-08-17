package com.daltao.client;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

public class SearchAllFile implements Callable<List<File>> {
    File rootDir;

    public SearchAllFile(File rootFile) {
        this.rootDir = rootFile;
    }

    private List<File> files;

    @Override
    public List<File> call() throws Exception {
        files = new ArrayList<>();
        dfs(rootDir);
        return files;
    }

    private void dfs(File dir) {
        for (File file : dir.listFiles()) {
            if (file.isDirectory()) {
                dfs(file);
            } else {
                files.add(file);
            }
        }
    }
}
