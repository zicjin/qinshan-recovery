/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.mobile.wechat.helper.encrypt;

import com.sunwayworld.cloud.module.mobile.wechat.helper.utils.Charsets;
import java.util.Arrays;

class PKCS7Encoder {
    static int BLOCK_SIZE = 32;

    PKCS7Encoder() {
    }

    static byte[] encode(int count) {
        int amountToPad = BLOCK_SIZE - count % BLOCK_SIZE;
        if (amountToPad == 0) {
            amountToPad = BLOCK_SIZE;
        }
        char padChr = PKCS7Encoder.chr(amountToPad);
        String tmp = new String();
        for (int index = 0; index < amountToPad; ++index) {
            tmp = tmp + padChr;
        }
        return tmp.getBytes(Charsets.UTF_8);
    }

    static byte[] decode(byte[] decrypted) {
        byte pad = decrypted[decrypted.length - 1];
        if (pad < 1 || pad > 32) {
            pad = 0;
        }
        return Arrays.copyOfRange(decrypted, 0, decrypted.length - pad);
    }

    static char chr(int a) {
        byte target = (byte)(a & 0xFF);
        return (char)target;
    }
}
