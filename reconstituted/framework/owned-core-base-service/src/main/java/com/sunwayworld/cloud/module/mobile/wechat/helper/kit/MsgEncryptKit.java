/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.mobile.wechat.helper.kit;

import com.sunwayworld.cloud.module.mobile.wechat.helper.encrypt.AesException;
import com.sunwayworld.cloud.module.mobile.wechat.helper.encrypt.WXBizMsgCrypt;
import com.sunwayworld.cloud.module.mobile.wechat.weixin.api.ApiConfig;
import com.sunwayworld.cloud.module.mobile.wechat.weixin.api.ApiConfigKit;
import java.io.IOException;
import java.io.StringReader;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

public class MsgEncryptKit {
    private static final String format = "<xml><ToUserName><![CDATA[toUser]]></ToUserName><Encrypt><![CDATA[%1$s]]></Encrypt></xml>";

    public static String encrypt(String msg, String timestamp, String nonce) {
        try {
            ApiConfig ac = ApiConfigKit.getApiConfig();
            return MsgEncryptKit.encryptMsg(msg, timestamp, nonce, ac.getAppId(), ac.getToken(), ac.getEncodingAesKey());
        }
        catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static String decrypt(String encryptedMsg, String timestamp, String nonce, String msgSignature) {
        try {
            ApiConfig ac = ApiConfigKit.getApiConfig();
            return MsgEncryptKit.decryptMsg(encryptedMsg, timestamp, nonce, msgSignature, ac.getAppId(), ac.getToken(), ac.getEncodingAesKey());
        }
        catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static String decryptMsg(String encryptedMsg, String timestamp, String nonce, String msgSignature, String appId, String token, String encodingAesKey) throws ParserConfigurationException, SAXException, IOException, AesException {
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        DocumentBuilder db = dbf.newDocumentBuilder();
        StringReader sr = new StringReader(encryptedMsg);
        InputSource is = new InputSource(sr);
        Document document = db.parse(is);
        Element root = document.getDocumentElement();
        NodeList nodelist1 = root.getElementsByTagName("Encrypt");
        String encrypt = nodelist1.item(0).getTextContent();
        String fromXML = String.format(format, encrypt);
        if (encodingAesKey == null) {
            throw new IllegalStateException("encodingAesKey can not be null, config encodingAesKey first.");
        }
        WXBizMsgCrypt pc = new WXBizMsgCrypt(token, encodingAesKey, appId);
        return pc.decryptMsg(msgSignature, timestamp, nonce, fromXML);
    }

    private static String encryptMsg(String msg, String timestamp, String nonce, String appId, String token, String encodingAesKey) throws AesException {
        WXBizMsgCrypt pc = new WXBizMsgCrypt(token, encodingAesKey, appId);
        return pc.encryptMsg(msg, timestamp, nonce);
    }
}
