package com.daltao.client;

import com.daltao.dto.Summary;
import com.daltao.utils.FileUtils;
import com.daltao.utils.RollingHashDeque;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

public class CalculateLocalSummary implements Callable<List<Summary>> {
    List<File> allFiles;
    int block;
    byte[] buf = new byte[1 << 13];
    RollingHashDeque dq;

    public CalculateLocalSummary(List<File> allFiles, int block, RollingHashDeque dq) {
        this.allFiles = allFiles;
        this.block = block;
        this.dq = dq;
    }

    List<Summary> summaries;

    @Override
    public List<Summary> call() throws Exception {
        summaries = new ArrayList<>();
        for (File file : allFiles) {
            singleFile(file);
        }
        return summaries;
    }

    private void flush() {
        if (dq.isEmpty()) {
            return;
        }
        Summary s = new Summary();
        s.id = summaries.size();
        s.hashV = dq.hashV();
        s.md5 = dq.md5();
        dq.clear();
    }

    private void singleFile(File file) throws IOException {
        FileUtils.iterateOverFile(file, buf, b -> {
            dq.addLast(b);
            if (dq.size() == block) {
                flush();
            }
        });
        flush();
    }

}
