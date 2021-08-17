package com.daltao.utils;

import java.io.IOException;
import java.io.OutputStream;

public class BufferedOS implements AutoCloseable {
    byte[] buf ;
    OutputStream os;
    int wpos = 0;

    public BufferedOS(byte[] buf){
        this.buf = buf;
    }

    public BufferedOS(byte[] buf, OutputStream os){
        this.buf = buf;
        setOS(os);
    }

    public BufferedOS(int size){
        this(new byte[size]);
    }

    public void setOS(OutputStream os) {
        this.os = os;
    }

    private void write(byte b) throws IOException {
        buf[wpos++] = b;
        if (wpos == buf.length) {
            flush(false);
        }
    }

    public void write(byte[] data, int offset, int length) throws IOException {
        for (int i = 0; i < length; i++) {
            write(data[i + offset]);
        }
    }

    public void close() throws IOException {
        flush(true);
        os.close();
    }

    public void flush(boolean flushOS) throws IOException {
        os.write(buf, 0, wpos);
        wpos = 0;
        if (flushOS) {
            os.flush();
        }
    }
}
