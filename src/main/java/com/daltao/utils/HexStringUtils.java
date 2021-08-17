package com.daltao.utils;

public class HexStringUtils {
    static char[] mapping = "0123456789abcdef".toCharArray();

    public static String toHexString(byte[] data) {
        char[] ans = new char[data.length * 2];
        for (int i = 0; i < data.length; i++) {
            ans[i * 2] = mapping[data[i] & 0xf];
            ans[i * 2 + 1] = mapping[(data[i] >> 4) & 0xf];
        }
        return new String(ans);
    }


    private static int getBack(int x) {
        if (x >= '0' && x <= '9') {
            return x - '0';
        }
        return x - 'a' + 10;
    }

    public static byte[] fromHexString(String s) {
        byte[] ans = new byte[s.length() / 2];
        for (int i = 0; i < s.length(); i += 2) {
            char c0 = s.charAt(i);
            char c1 = s.charAt(i + 1);
            ans[i / 2] = (byte) (getBack(c0) | (getBack(c1) << 4));
        }
        return ans;
    }
}
