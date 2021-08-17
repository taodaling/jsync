package com.daltao.server;

import com.daltao.dto.Directory;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

public class CalculateFileStructureTask implements Callable<List<Directory>> {
    File rootFile;

    public CalculateFileStructureTask(File rootFile) {
        this.rootFile = rootFile;
    }

    public List<Directory> call() {
        accum = new ArrayList<>();
        int root = create(rootFile.getName(), -1);
        dfs(root, rootFile);
        return accum;
    }

    List<Directory> accum;

    private int create(String name, int fa) {
        Directory d = new Directory();
        d.name = name;
        d.parentId = fa;
        accum.add(d);
        return accum.size() - 1;
    }

    private void dfs(int fa, File dir) {
        for (File file : dir.listFiles(f -> f.isDirectory())) {
            int id = create(file.getName(), fa);
            dfs(id, file);
        }
    }
}
