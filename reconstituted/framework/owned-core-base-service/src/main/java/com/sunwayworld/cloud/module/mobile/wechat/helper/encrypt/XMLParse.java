/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.mobile.wechat.helper.encrypt;

import com.sunwayworld.cloud.module.mobile.wechat.helper.encrypt.AesException;
import com.sunwayworld.cloud.module.mobile.wechat.helper.utils.XmlHelper;

class XMLParse {
    XMLParse() {
    }

    public static Object[] extract(String xmlStr) throws AesException {
        Object[] result = new Object[3];
        try {
            XmlHelper xmlHelper = XmlHelper.of(xmlStr);
            result[0] = 0;
            result[1] = xmlHelper.getString("//Encrypt");
            result[2] = xmlHelper.getString("//ToUserName");
            return result;
        }
        catch (Exception e) {
            throw new AesException(-40002);
        }
    }

    public static String generate(String encrypt, String signature, String timestamp, String nonce) {
        String format = "<xml>\n<Encrypt><![CDATA[%1$s]]></Encrypt>\n<MsgSignature><![CDATA[%2$s]]></MsgSignature>\n<TimeStamp>%3$s</TimeStamp>\n<Nonce><![CDATA[%4$s]]></Nonce>\n</xml>";
        return String.format(format, encrypt, signature, timestamp, nonce);
    }
}
