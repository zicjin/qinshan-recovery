/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.mobile.wechat.helper.utils;

import java.security.MessageDigest;
import java.security.SecureRandom;

public class HashKit {
    private static final SecureRandom random = new SecureRandom();
    private static final char[] HEX_DIGITS = "0123456789abcdef".toCharArray();
    private static final char[] CHAR_ARRAY = "_-0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ".toCharArray();

    public static String md5(String srcStr) {
        return HashKit.hash("MD5", srcStr);
    }

    public static String sha1(String srcStr) {
        return HashKit.hash("SHA-1", srcStr);
    }

    public static String sha256(String srcStr) {
        return HashKit.hash("SHA-256", srcStr);
    }

    public static String sha384(String srcStr) {
        return HashKit.hash("SHA-384", srcStr);
    }

    public static String sha512(String srcStr) {
        return HashKit.hash("SHA-512", srcStr);
    }

    public static String hash(String algorithm, String srcStr) {
        try {
            MessageDigest md = MessageDigest.getInstance(algorithm);
            byte[] bytes = md.digest(srcStr.getBytes("utf-8"));
            return HashKit.toHex(bytes);
        }
        catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static String toHex(byte[] bytes) {
        StringBuilder ret = new StringBuilder(bytes.length * 2);
        for (int i = 0; i < bytes.length; ++i) {
            ret.append(HEX_DIGITS[bytes[i] >> 4 & 0xF]);
            ret.append(HEX_DIGITS[bytes[i] & 0xF]);
        }
        return ret.toString();
    }

    public static String generateSalt(int saltLength) {
        StringBuilder salt = new StringBuilder();
        for (int i = 0; i < saltLength; ++i) {
            salt.append(CHAR_ARRAY[random.nextInt(CHAR_ARRAY.length)]);
        }
        return salt.toString();
    }

    public static String generateSaltForSha256() {
        return HashKit.generateSalt(32);
    }

    public static String generateSaltForSha512() {
        return HashKit.generateSalt(64);
    }

    public static boolean slowEquals(byte[] a, byte[] b) {
        if (a == null || b == null) {
            return false;
        }
        int diff = a.length ^ b.length;
        for (int i = 0; i < a.length && i < b.length; ++i) {
            diff |= a[i] ^ b[i];
        }
        return diff == 0;
    }
}
