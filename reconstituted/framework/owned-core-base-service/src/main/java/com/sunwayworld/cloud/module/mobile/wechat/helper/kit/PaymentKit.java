/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.mobile.wechat.helper.kit;

import com.sunwayworld.cloud.module.mobile.wechat.helper.utils.Charsets;
import com.sunwayworld.cloud.module.mobile.wechat.helper.utils.HashKit;
import com.sunwayworld.cloud.module.mobile.wechat.helper.utils.XmlHelper;
import com.sunwayworld.framework.utils.StringUtils;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Map;
import java.util.TreeMap;

public class PaymentKit {
    public static String packageSign(Map<String, String> params, boolean urlEncoder) {
        TreeMap<String, String> sortedParams = new TreeMap<String, String>(params);
        StringBuilder sb = new StringBuilder();
        boolean first = true;
        for (Map.Entry<String, String> param : sortedParams.entrySet()) {
            String value = param.getValue();
            if (StringUtils.isBlank(value)) continue;
            if (first) {
                first = false;
            } else {
                sb.append("&");
            }
            sb.append(param.getKey()).append("=");
            if (urlEncoder) {
                try {
                    value = PaymentKit.urlEncode(value);
                }
                catch (UnsupportedEncodingException unsupportedEncodingException) {
                    // empty catch block
                }
            }
            sb.append(value);
        }
        return sb.toString();
    }

    public static String urlEncode(String src) throws UnsupportedEncodingException {
        return URLEncoder.encode(src, Charsets.UTF_8.name()).replace("+", "%20");
    }

    public static String createSign(Map<String, String> params, String paternerKey) {
        params.remove("sign");
        String stringA = PaymentKit.packageSign(params, false);
        String stringSignTemp = stringA + "&key=" + paternerKey;
        return HashKit.md5(stringSignTemp).toUpperCase();
    }

    public static boolean verifyNotify(Map<String, String> params, String paternerKey) {
        String sign = params.get("sign");
        String localSign = PaymentKit.createSign(params, paternerKey);
        return sign.equals(localSign);
    }

    public static String toXml(Map<String, String> params) {
        StringBuilder xml = new StringBuilder();
        xml.append("<xml>");
        for (Map.Entry<String, String> entry : params.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();
            if (StringUtils.isBlank(value)) continue;
            xml.append("<").append(key).append(">");
            xml.append(entry.getValue());
            xml.append("</").append(key).append(">");
        }
        xml.append("</xml>");
        return xml.toString();
    }

    public static Map<String, String> xmlToMap(String xmlStr) {
        XmlHelper xmlHelper = XmlHelper.of(xmlStr);
        return xmlHelper.toMap();
    }
}
