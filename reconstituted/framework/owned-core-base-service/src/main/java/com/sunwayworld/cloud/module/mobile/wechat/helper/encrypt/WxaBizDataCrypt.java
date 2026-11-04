/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.mobile.wechat.helper.encrypt;

import com.sunwayworld.cloud.module.mobile.wechat.helper.encrypt.PKCS7Encoder;
import com.sunwayworld.cloud.module.mobile.wechat.helper.utils.Charsets;
import java.security.AlgorithmParameters;
import java.security.Key;
import java.util.Base64;
import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public class WxaBizDataCrypt {
    private final String sessionKey;

    public WxaBizDataCrypt(String sessionKey) {
        this.sessionKey = sessionKey;
    }

    public String decrypt(String encryptedData, String ivStr) {
        byte[] bizData = Base64.getDecoder().decode(encryptedData);
        byte[] keyByte = Base64.getDecoder().decode(this.sessionKey);
        byte[] ivByte = Base64.getDecoder().decode(ivStr);
        try {
            Cipher cipher = Cipher.getInstance("AES/CBC/NoPadding");
            SecretKeySpec sKeySpec = new SecretKeySpec(keyByte, "AES");
            AlgorithmParameters params = AlgorithmParameters.getInstance("AES");
            params.init(new IvParameterSpec(ivByte));
            cipher.init(2, (Key)sKeySpec, params);
            byte[] original = cipher.doFinal(bizData);
            byte[] result = PKCS7Encoder.decode(original);
            return new String(result, Charsets.UTF_8);
        }
        catch (Exception e) {
            throw new RuntimeException("aes\u89e3\u5bc6\u5931\u8d25");
        }
    }
}
