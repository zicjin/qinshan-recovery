/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.mobile.wechat.weixin.api;

import com.sunwayworld.cloud.module.mobile.wechat.helper.kit.PaymentKit;
import com.sunwayworld.cloud.module.mobile.wechat.helper.utils.HttpUtils;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

public class PaymentApi {
    private static String unifiedOrderUrl = "https://api.mch.weixin.qq.com/pay/unifiedorder";
    private static String orderQueryUrl = "https://api.mch.weixin.qq.com/pay/orderquery";
    private static String closeOrderUrl = "https://api.mch.weixin.qq.com/pay/closeorder";
    public static String refundUrl = "https://api.mch.weixin.qq.com/secapi/pay/refund";
    private static String refundQueryUrl = "https://api.mch.weixin.qq.com/pay/refundquery";
    private static String downloadBillUrl = "https://api.mch.weixin.qq.com/pay/downloadbill";

    private PaymentApi() {
    }

    public static String pushOrder(Map<String, String> params) {
        return HttpUtils.post(unifiedOrderUrl, PaymentKit.toXml(params));
    }

    private static Map<String, String> request(String url, Map<String, String> params, String paternerKey) {
        params.put("nonce_str", System.currentTimeMillis() + "");
        String sign = PaymentKit.createSign(params, paternerKey);
        params.put("sign", sign);
        String xmlStr = HttpUtils.post(url, PaymentKit.toXml(params));
        return PaymentKit.xmlToMap(xmlStr);
    }

    public static String getDeepLink(String appId, String prepayId, String paternerKey) {
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("appid", appId);
        params.put("noncestr", System.currentTimeMillis() + "");
        params.put("package", "WAP");
        params.put("prepayid", prepayId);
        params.put("timestamp", System.currentTimeMillis() / 1000L + "");
        String sign = PaymentKit.createSign(params, paternerKey);
        params.put("sign", sign);
        String string1 = PaymentKit.packageSign(params, true);
        String string2 = "";
        try {
            string2 = PaymentKit.urlEncode(string1);
        }
        catch (UnsupportedEncodingException unsupportedEncodingException) {
            // empty catch block
        }
        return "weixin://wap/pay?" + string2;
    }

    public static Map<String, String> queryByTransactionId(String appid, String mch_id, String paternerKey, String transaction_id) {
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("appid", appid);
        params.put("mch_id", mch_id);
        params.put("transaction_id", transaction_id);
        return PaymentApi.request(orderQueryUrl, params, paternerKey);
    }

    public static Map<String, String> queryByOutTradeNo(String appid, String mch_id, String paternerKey, String out_trade_no) {
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("appid", appid);
        params.put("mch_id", mch_id);
        params.put("out_trade_no", out_trade_no);
        return PaymentApi.request(orderQueryUrl, params, paternerKey);
    }

    public static Map<String, String> closeOrder(String appid, String mch_id, String paternerKey, String out_trade_no) {
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("appid", appid);
        params.put("mch_id", mch_id);
        params.put("out_trade_no", out_trade_no);
        return PaymentApi.request(closeOrderUrl, params, paternerKey);
    }

    public static Map<String, String> refund(Map<String, String> params, String paternerKey, String certPath) {
        params.put("nonce_str", System.currentTimeMillis() + "");
        String sign = PaymentKit.createSign(params, paternerKey);
        params.put("sign", sign);
        String partner = params.get("mch_id");
        String xmlStr = HttpUtils.postSSL(refundUrl, PaymentKit.toXml(params), certPath, partner);
        return PaymentKit.xmlToMap(xmlStr);
    }

    private static Map<String, String> baseRefundQuery(Map<String, String> params, String appid, String mch_id, String paternerKey) {
        params.put("appid", appid);
        params.put("mch_id", mch_id);
        return PaymentApi.request(refundQueryUrl, params, paternerKey);
    }

    public static Map<String, String> refundQueryByTransactionId(String appid, String mch_id, String paternerKey, String transaction_id) {
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("transaction_id", transaction_id);
        return PaymentApi.baseRefundQuery(params, appid, mch_id, paternerKey);
    }

    public static Map<String, String> refundQueryByOutTradeNo(String appid, String mch_id, String paternerKey, String out_trade_no) {
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("out_trade_no", out_trade_no);
        return PaymentApi.baseRefundQuery(params, appid, mch_id, paternerKey);
    }

    public static Map<String, String> refundQueryByOutRefundNo(String appid, String mch_id, String paternerKey, String out_refund_no) {
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("out_refund_no", out_refund_no);
        return PaymentApi.baseRefundQuery(params, appid, mch_id, paternerKey);
    }

    public static Map<String, String> refundQueryByRefundId(String appid, String mch_id, String paternerKey, String refund_id) {
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("refund_id", refund_id);
        return PaymentApi.baseRefundQuery(params, appid, mch_id, paternerKey);
    }

    public static String downloadBill(String appid, String mch_id, String paternerKey, String billDate) {
        return PaymentApi.downloadBill(appid, mch_id, paternerKey, billDate, null);
    }

    public static String downloadBill(String appid, String mch_id, String paternerKey, String billDate, BillType billType) {
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("appid", appid);
        params.put("mch_id", mch_id);
        params.put("nonce_str", System.currentTimeMillis() + "");
        params.put("bill_date", billDate);
        if (null != billType) {
            params.put("bill_type", billType.name());
        } else {
            params.put("bill_type", BillType.ALL.name());
        }
        String sign = PaymentKit.createSign(params, paternerKey);
        params.put("sign", sign);
        return HttpUtils.post(downloadBillUrl, PaymentKit.toXml(params));
    }

    public static enum BillType {
        ALL,
        SUCCESS,
        REFUND,
        REVOKED;

    }

    public static enum TradeType {
        JSAPI,
        NATIVE,
        APP,
        WAP,
        MWEB;

    }
}
