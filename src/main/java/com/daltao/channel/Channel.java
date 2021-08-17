package com.daltao.channel;

import java.io.DataInputStream;
import java.io.DataOutputStream;

public interface Channel<T> {
    void write(DataOutputStream dos, T data) throws Exception;
    T read(DataInputStream dis) throws Exception;
}
