package com.daltao.utils;

import javax.crypto.*;
import javax.crypto.spec.SecretKeySpec;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;

public class AESUtils {
    public static void main(String[] args) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException, BadPaddingException, IllegalBlockSizeException, IOException {
        String pwd = extractPwd("123");
        Cipher encrypt = create(pwd, true);
        Cipher decrypt = create(pwd, false);
        byte[] mid = encrypt.doFinal("Hello".getBytes(StandardCharsets.UTF_8));
        byte[] ans = decrypt.doFinal(mid);
        System.out.println(new String(ans));

        ByteArrayOutputStream os = new ByteArrayOutputStream();
        OutputStream out = encrypt(pwd, os);
        out.write("Hello".getBytes(StandardCharsets.UTF_8));
        out.close();

        ByteArrayInputStream is = new ByteArrayInputStream(os.toByteArray());
        InputStream in = decrypt(pwd, is);
        byte[] data = in.readAllBytes();
        System.out.println(new String(data));
    }


    public static String extractPwd(String pwd) {
        if (pwd.length() == 32) {
            return pwd;
        }
        StringBuilder ans = new StringBuilder(pwd);
        while (ans.length() < 32) {
            ans.append(" ");
        }
        ans.setLength(32);
        return ans.toString();
    }

    public static Cipher create(String pwd, boolean encrypt) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException {
        pwd = extractPwd(pwd);
        byte[] byteKeys = HexStringUtils.fromHexString(pwd);
        Key key = new SecretKeySpec(byteKeys, "AES");
        Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
        int mode = encrypt ? Cipher.ENCRYPT_MODE : Cipher.DECRYPT_MODE;
        cipher.init(mode, key);
        return cipher;
    }

    public static OutputStream encrypt(String pwd, OutputStream os) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException {
//        return os;
        return new CipherOutputStream(os, create(pwd, true));
    }

    public static InputStream decrypt(String pwd, InputStream is) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException {
//        return is;
        return new CipherInputStream(is, create(pwd, false));
    }
}
