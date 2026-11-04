/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.mobile.wechat.weixin.api;

import com.sunwayworld.cloud.module.mobile.wechat.helper.kit.PaymentKit;
import com.sunwayworld.cloud.module.mobile.wechat.helper.utils.HttpUtils;
import java.util.Map;

public class RedPackApi {
    private static String sendRedPackUrl = "https://api.mch.weixin.qq.com/mmpaymkttransfers/sendredpack";
    private static String getHBInfo = "https://api.mch.weixin.qq.com/mmpaymkttransfers/gethbinfo ";
    private static String sendGroupRedPackUrl = "https://api.mch.weixin.qq.com/mmpaymkttransfers/sendgroupredpack";

    public static String sendRedPack(Map<String, String> params, String certPath, String partner) {
        return HttpUtils.postSSL(sendRedPackUrl, PaymentKit.toXml(params), certPath, partner);
    }

    public static String getHbInfo(Map<String, String> params, String certPath, String partner) {
        return HttpUtils.postSSL(getHBInfo, PaymentKit.toXml(params), certPath, partner);
    }

    public static String sendGroupRedPack(Map<String, String> params, String certPath, String partner) {
        return HttpUtils.postSSL(sendGroupRedPackUrl, PaymentKit.toXml(params), certPath, partner);
    }
}
