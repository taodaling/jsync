package com.daltao.client;

import com.daltao.dto.SummaryClient;
import com.daltao.utils.FileUtils;
import com.daltao.utils.FileVisitor;
import com.daltao.utils.RollingHashDeque;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

public class CalculateLocalSummary implements Callable<List<SummaryClient>> {
    int block;
    byte[] buf = new byte[1 << 13];
    RollingHashDeque dq;
    File root;

    public CalculateLocalSummary(File root, int block, RollingHashDeque dq) {
        this.root = root;
        this.block = block;
        this.dq = dq;
    }

    List<SummaryClient> summaries;

    @Override
    public List<SummaryClient> call() throws Exception {
        summaries = new ArrayList<>();
        FileUtils.visitFileTree(root, new FileVisitor() {
            @Override
            public void enterDirectory(File dir) {

            }

            @Override
            public void leaveDirectory(File dir) {

            }

            @Override
            public void visitFile(File file) throws Exception {
                singleFile(file);
            }
        });
        return summaries;
    }

    long curOffset;
    File file;

    private void flush() {
        if (dq.isEmpty()) {
            return;
        }
        SummaryClient s = new SummaryClient();
        s.hashV = dq.hashV();
        s.md5 = dq.md5();
        s.offset = curOffset - dq.size();
        s.length = dq.size();
        s.file = file;
        dq.clear();
        summaries.add(s);
    }

    private void singleFile(File file) throws Exception {
        curOffset = 0;
        this.file = file;
        FileUtils.iterateOverFile(file, buf, b -> {
            curOffset++;
            dq.addLast(b);
            if (dq.size() == block) {
                flush();
            }
        });
        flush();
    }

}
