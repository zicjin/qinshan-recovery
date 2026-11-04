/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.mobile.wechat.helper.kit;

import java.util.Random;

public class RandomKit {
    private static final char[] CHAR_ARRAY = "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ".toCharArray();
    private static final Random random = new Random();

    public static String gen(int len) {
        StringBuilder salt = new StringBuilder(len);
        for (int i = 0; i < len; ++i) {
            salt.append(CHAR_ARRAY[random.nextInt(CHAR_ARRAY.length)]);
        }
        return salt.toString();
    }

    public static String genNonceStr() {
        return RandomKit.gen(16);
    }
}
