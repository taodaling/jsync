package com.daltao.utils;

public interface HashDeque {
    public void clear();

    public void addLast(byte x);

    public byte removeLast();

    public void addFirst(byte x) ;

    public byte removeFirst() ;

    public int size() ;

    public boolean isEmpty() ;

    public long hash() ;

    public long hashV() ;

    public byte[] md5();
}
