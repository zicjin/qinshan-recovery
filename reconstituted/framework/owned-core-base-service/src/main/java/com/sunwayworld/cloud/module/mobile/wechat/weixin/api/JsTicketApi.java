/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.mobile.wechat.weixin.api;

import com.sunwayworld.cloud.module.mobile.wechat.helper.bean.JsTicket;
import com.sunwayworld.cloud.module.mobile.wechat.helper.cache.IAccessTokenCache;
import com.sunwayworld.cloud.module.mobile.wechat.helper.kit.ParaMap;
import com.sunwayworld.cloud.module.mobile.wechat.helper.utils.HttpUtils;
import com.sunwayworld.cloud.module.mobile.wechat.helper.utils.RetryUtils;
import com.sunwayworld.cloud.module.mobile.wechat.weixin.api.AccessTokenApi;
import com.sunwayworld.cloud.module.mobile.wechat.weixin.api.ApiConfigKit;
import com.sunwayworld.framework.utils.StringUtils;
import java.util.concurrent.Callable;

public class JsTicketApi {
    private static String apiUrl = "https://api.weixin.qq.com/cgi-bin/ticket/getticket";

    public static JsTicket getTicket(JsApiType jsApiType) {
        String access_token = AccessTokenApi.getAccessTokenStr();
        String appId = ApiConfigKit.getApiConfig().getAppId();
        String key = appId + ':' + jsApiType.name();
        final ParaMap pm = ParaMap.create("access_token", access_token).put("type", jsApiType.name());
        IAccessTokenCache accessTokenCache = ApiConfigKit.getAccessTokenCache();
        String jsTicketJson = accessTokenCache.get(key);
        JsTicket jsTicket = null;
        if (!StringUtils.isBlank(jsTicketJson)) {
            jsTicket = new JsTicket(jsTicketJson);
        }
        if (null == jsTicket || !jsTicket.isAvailable()) {
            jsTicket = RetryUtils.retryOnException(3, new Callable<JsTicket>(){

                @Override
                public JsTicket call() throws Exception {
                    return new JsTicket(HttpUtils.get(apiUrl, pm.getData()));
                }
            });
            if (null != jsApiType) {
                accessTokenCache.set(key, jsTicket.getCacheJson());
            }
        }
        return jsTicket;
    }

    public static enum JsApiType {
        jsapi,
        wx_card;

    }
}
