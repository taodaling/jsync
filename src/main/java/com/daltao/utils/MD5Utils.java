package com.daltao.utils;

import java.security.DigestException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class MD5Utils {
    private static ThreadLocal<MessageDigest> mdTL = ThreadLocal.withInitial(MD5Utils::create);

    public static MessageDigest create(){
        MessageDigest md = null;
        try {
            md = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException(e);
        }
        return md;
    }

    public static byte[] md5(byte[] data, int offset, int length) {
        MessageDigest md = mdTL.get();
        md.reset();
        try {
            md.digest(data, offset, length);
            return md.digest();
        } catch (DigestException e) {
            throw new IllegalStateException(e);
        }
    }
}
