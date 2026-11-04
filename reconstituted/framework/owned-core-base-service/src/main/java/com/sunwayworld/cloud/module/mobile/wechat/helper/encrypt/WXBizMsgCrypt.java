/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.mobile.wechat.helper.encrypt;

import com.sunwayworld.cloud.module.mobile.wechat.helper.encrypt.AesException;
import com.sunwayworld.cloud.module.mobile.wechat.helper.encrypt.ByteGroup;
import com.sunwayworld.cloud.module.mobile.wechat.helper.encrypt.PKCS7Encoder;
import com.sunwayworld.cloud.module.mobile.wechat.helper.encrypt.SHA1;
import com.sunwayworld.cloud.module.mobile.wechat.helper.encrypt.XMLParse;
import com.sunwayworld.cloud.module.mobile.wechat.helper.utils.Charsets;
import com.sunwayworld.framework.utils.StringUtils;
import java.security.Key;
import java.util.Arrays;
import java.util.Base64;
import java.util.Random;
import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public class WXBizMsgCrypt {
    byte[] aesKey;
    String token;
    String appId;

    public WXBizMsgCrypt(String token, String encodingAesKey, String appId) throws AesException {
        if (encodingAesKey.length() != 43) {
            throw new AesException(-40004);
        }
        this.token = token;
        this.appId = appId;
        this.aesKey = Base64.getDecoder().decode(encodingAesKey + "=");
    }

    byte[] getNetworkBytesOrder(int sourceNumber) {
        byte[] orderBytes = new byte[4];
        orderBytes[3] = (byte)(sourceNumber & 0xFF);
        orderBytes[2] = (byte)(sourceNumber >> 8 & 0xFF);
        orderBytes[1] = (byte)(sourceNumber >> 16 & 0xFF);
        orderBytes[0] = (byte)(sourceNumber >> 24 & 0xFF);
        return orderBytes;
    }

    int recoverNetworkBytesOrder(byte[] orderBytes) {
        int sourceNumber = 0;
        for (int i = 0; i < 4; ++i) {
            sourceNumber <<= 8;
            sourceNumber |= orderBytes[i] & 0xFF;
        }
        return sourceNumber;
    }

    String getRandomStr() {
        String base = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        Random random = new Random();
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < 16; ++i) {
            int number = random.nextInt(base.length());
            sb.append(base.charAt(number));
        }
        return sb.toString();
    }

    String encrypt(String randomStr, String text) throws AesException {
        ByteGroup byteCollector = new ByteGroup();
        byte[] randomStrBytes = randomStr.getBytes(Charsets.UTF_8);
        byte[] textBytes = text.getBytes(Charsets.UTF_8);
        byte[] networkBytesOrder = this.getNetworkBytesOrder(textBytes.length);
        byte[] appidBytes = this.appId.getBytes(Charsets.UTF_8);
        byteCollector.addBytes(randomStrBytes);
        byteCollector.addBytes(networkBytesOrder);
        byteCollector.addBytes(textBytes);
        byteCollector.addBytes(appidBytes);
        byte[] padBytes = PKCS7Encoder.encode(byteCollector.size());
        byteCollector.addBytes(padBytes);
        byte[] unencrypted = byteCollector.toBytes();
        try {
            Cipher cipher = Cipher.getInstance("AES/CBC/NoPadding");
            SecretKeySpec keySpec = new SecretKeySpec(this.aesKey, "AES");
            IvParameterSpec iv = new IvParameterSpec(this.aesKey, 0, 16);
            cipher.init(1, (Key)keySpec, iv);
            byte[] encrypted = cipher.doFinal(unencrypted);
            String base64Encrypted = new String(Base64.getEncoder().encode(encrypted), "UTF-8");
            return base64Encrypted;
        }
        catch (Exception e) {
            throw new AesException(-40006);
        }
    }

    String decrypt(String text) throws AesException {
        String from_appid;
        String xmlContent;
        byte[] original;
        try {
            Cipher cipher = Cipher.getInstance("AES/CBC/NoPadding");
            SecretKeySpec key_spec = new SecretKeySpec(this.aesKey, "AES");
            IvParameterSpec iv = new IvParameterSpec(Arrays.copyOfRange(this.aesKey, 0, 16));
            cipher.init(2, (Key)key_spec, iv);
            byte[] encrypted = Base64.getDecoder().decode(text);
            original = cipher.doFinal(encrypted);
        }
        catch (Exception e) {
            throw new AesException(-40007);
        }
        try {
            byte[] bytes = PKCS7Encoder.decode(original);
            byte[] networkOrder = Arrays.copyOfRange(bytes, 16, 20);
            int xmlLength = this.recoverNetworkBytesOrder(networkOrder);
            xmlContent = new String(Arrays.copyOfRange(bytes, 20, 20 + xmlLength), Charsets.UTF_8);
            from_appid = new String(Arrays.copyOfRange(bytes, 20 + xmlLength, bytes.length), Charsets.UTF_8);
        }
        catch (Exception e) {
            throw new AesException(-40008);
        }
        if (!from_appid.equals(this.appId)) {
            throw new AesException(-40005);
        }
        return xmlContent;
    }

    public String encryptMsg(String replyMsg, String timeStamp, String nonce) throws AesException {
        String encrypt = this.encrypt(this.getRandomStr(), replyMsg);
        if (StringUtils.isBlank(timeStamp)) {
            timeStamp = Long.toString(System.currentTimeMillis());
        }
        String signature = SHA1.getSHA1(this.token, timeStamp, nonce, encrypt);
        String result = XMLParse.generate(encrypt, signature, timeStamp, nonce);
        return result;
    }

    public String decryptMsg(String msgSignature, String timeStamp, String nonce, String postData) throws AesException {
        Object[] encrypt = XMLParse.extract(postData);
        String signature = SHA1.getSHA1(this.token, timeStamp, nonce, encrypt[1].toString());
        if (!signature.equals(msgSignature)) {
            throw new AesException(-40001);
        }
        String result = this.decrypt(encrypt[1].toString());
        return result;
    }

    public String verifyUrl(String msgSignature, String timeStamp, String nonce, String echoStr) throws AesException {
        String signature = SHA1.getSHA1(this.token, timeStamp, nonce, echoStr);
        if (!signature.equals(msgSignature)) {
            throw new AesException(-40001);
        }
        String result = this.decrypt(echoStr);
        return result;
    }
}
