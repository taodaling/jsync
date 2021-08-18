package com.daltao.utils;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NullCipher;
import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.OutputStream;

public class NotCloseCipherOutputStream
        extends FilterOutputStream {
    private Cipher cipher;
    private OutputStream output;
    private byte[] ibuffer = new byte[1];
    private byte[] obuffer;
    private boolean closed = false;

    public NotCloseCipherOutputStream(OutputStream os, Cipher c) {
        super(os);
        this.output = os;
        this.cipher = c;
    }

    protected NotCloseCipherOutputStream(OutputStream os) {
        super(os);
        this.output = os;
        this.cipher = new NullCipher();
    }

    public void write(int b) throws IOException {
        this.ibuffer[0] = (byte) b;
        this.obuffer = this.cipher.update(this.ibuffer, 0, 1);
        if (this.obuffer != null) {
            this.output.write(this.obuffer);
            this.obuffer = null;
        }

    }

    public void write(byte[] b) throws IOException {
        this.write(b, 0, b.length);
    }

    public void write(byte[] b, int off, int len) throws IOException {
        this.obuffer = this.cipher.update(b, off, len);
        if (this.obuffer != null) {
            this.output.write(this.obuffer);
            this.obuffer = null;
        }

    }

    public void flush() throws IOException {
        if (this.obuffer != null) {
            this.output.write(this.obuffer);
            this.obuffer = null;
        }

        this.output.flush();
    }

    public void close() throws IOException {
        if (!this.closed) {
            this.closed = true;

            try {
                this.obuffer = this.cipher.doFinal();
            } catch (BadPaddingException | IllegalBlockSizeException var3) {
                this.obuffer = null;
            }

            try {
                this.flush();
            } catch (IOException var2) {
            }

//            this.out.close();
        }
    }
}
