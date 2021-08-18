package com.daltao.channel;

import com.daltao.dto.Summary;
import com.daltao.dto.SummaryClient;
import com.daltao.dto.SummaryServer;
import com.daltao.utils.*;
import com.google.common.hash.BloomFilter;
import com.google.common.hash.Funnels;

import java.io.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DeltaChannel {
    Map<SummaryKey, Summary> map;
    BloomFilter<Long> bf;
    int block;
    HashDeque dq;
    byte[] buf;
    byte[] appendBytes = new byte[128];
    int appendBytesWpos;
    byte[] bufOS = new byte[1 << 13];
    List<SummaryClient> sc;
    FileCache fc;

    public static interface Callback {
        void begin(File file);

        void end(File file);

        void process(long write, long transfer);
    }

    Callback cb = new Callback() {
        @Override
        public void begin(File file) {

        }

        @Override
        public void end(File file) {

        }

        @Override
        public void process(long write, long transfer) {

        }
    };

    public DeltaChannel setCallback(Callback cb) {
        this.cb = cb;
        return this;
    }

    private DeltaChannel(Map<SummaryKey, Summary> map, BloomFilter<Long> bf, int block, HashDeque dq) {
        this.map = map;
        this.bf = bf;
        this.block = block;
        this.dq = dq;
        buf = new byte[block];
    }

    public static DeltaChannel of(List<Summary> list, int block, HashDeque dq) {
        BloomFilter<Long> bf = BloomFilter.create(Funnels.longFunnel(), list.size(), 1e-6);
        Map<SummaryKey, Summary> map = new HashMap<>(list.size());
        for (Summary s : list) {
            bf.put(s.hashV);
            map.put(new SummaryKey(s.hashV, s.md5), s);
        }
        return new DeltaChannel(map, bf, block, dq);
    }

    final byte COPY = 1;
    final byte FILE_BEGIN = 2;
    final byte FILE_END = 3;
    final byte GO_BACK = 4;
    final byte CREATE_NEW_DIR_AND_ENTER = 5;

    private void flushAppendBytes(DataOutputStream dos) throws IOException {
        if (appendBytesWpos == 0) {
            return;
        }
        dos.writeByte(-appendBytesWpos);
        dos.write(appendBytes, 0, appendBytesWpos);
        appendBytesWpos = 0;
    }

    private void append(DataOutputStream dos, byte b) throws IOException {
        if (appendBytesWpos == appendBytes.length) {
            //flush
            flushAppendBytes(dos);
        }
        appendBytes[appendBytesWpos++] = b;
    }

    public void checkForReplacement(DataOutputStream dos) throws IOException {
        if (dq.isEmpty()) {
            return;
        }
        long hv = dq.hashV();
        if (!bf.mightContain(hv)) {
            return;
        }
        SummaryKey key = new SummaryKey(hv, dq.md5());
        Summary summary = map.get(key);
        if (summary == null) {
            return;
        }
        flushAppendBytes(dos);
        dos.writeByte(COPY);
        dos.writeInt(((SummaryServer) summary).id);
        dq.clear();
    }

    public void write(DataOutputStream dos, File data) throws Exception {
        FileUtils.visitFileTree(data, new FileVisitor() {
            @Override
            public void enterDirectory(File dir) throws IOException {
                //create dir
                dos.writeByte(CREATE_NEW_DIR_AND_ENTER);
                dos.writeUTF(dir.getName());
            }

            @Override
            public void leaveDirectory(File dir) throws IOException {
                dos.writeByte(GO_BACK);
            }

            @Override
            public void visitFile(File file) throws Exception {
                dos.writeByte(FILE_BEGIN);
                dos.writeUTF(file.getName());
                dq.clear();
                FileUtils.iterateOverFile(file, buf, b -> {
                    if (dq.size() == block) {
                        append(dos, dq.removeFirst());
                    }
                    dq.addLast(b);
                    if (dq.size() == block) {
                        checkForReplacement(dos);
                    }
                });
                checkForReplacement(dos);
                while (!dq.isEmpty()) {
                    append(dos, dq.removeFirst());
                }
                flushAppendBytes(dos);
                dos.writeByte(FILE_END);
            }
        });
    }

    public void read(DataInputStream dis, File file, List<SummaryClient> sc,
                     FileCache fc) throws Exception {
        this.sc = sc;
        this.fc = fc;
        file.mkdirs();
        byte firstCmd = dis.readByte();
        if (firstCmd != CREATE_NEW_DIR_AND_ENTER) {
            throw new IllegalStateException();
        }
        String name = dis.readUTF();
        dfs(dis, file);
    }

    public long totalWrite;
    public long totalTransfer;

    private void generateFile(DataInputStream dis, File file) throws IOException {
        if (!file.createNewFile()) {
            throw new IllegalStateException();
        }
        long localWrite = 0;
        long localTransfer = 0;
        cb.begin(file);
        try (BufferedOS fos = new BufferedOS(bufOS, new FileOutputStream(file))) {
            int cmd;
            while ((cmd = dis.readByte()) != FILE_END) {
                if (cmd < 0) {
                    //add
                    dis.readNBytes(buf, 0, -cmd);
                    fos.write(buf, 0, -cmd);
                    localWrite += -cmd;
                    localTransfer += -cmd + 1;
                } else if (cmd == COPY) {
                    int id = dis.readInt();
                    SummaryClient summary = sc.get(id);
                    RandomAccessFile is = fc.computeIfAbsent(summary.file, k -> {
                        try {
                            return new RandomAccessFile(k, "r");
                        } catch (FileNotFoundException e) {
                            throw new UncheckedIOException(e);
                        }
                    });
                    is.seek(summary.offset);
                    is.readFully(buf, 0, summary.length);
                    fos.write(buf, 0, summary.length);
                    localWrite += summary.length;
                    localTransfer += 5;
                }
                cb.process(totalWrite, totalTransfer);
            }
        }

        cb.end(file);
        totalWrite += localWrite;
        totalTransfer += localTransfer;
    }

    private void dfs(DataInputStream dis, File parent) throws Exception {
        int cmd;
        while ((cmd = dis.readByte()) != GO_BACK) {
            if (cmd == CREATE_NEW_DIR_AND_ENTER) {
                String name = dis.readUTF();
                File dir = new File(parent, name);
                dir.mkdir();
                dfs(dis, dir);
            } else if (cmd == FILE_BEGIN) {
                //cool
                String name = dis.readUTF();
                File file = new File(parent, name);
                generateFile(dis, file);
            } else {
                throw new UnsupportedOperationException();
            }
        }
    }
}
