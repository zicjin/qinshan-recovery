/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.mobile.wechat.kfweixin.api;

import com.sunwayworld.cloud.module.mobile.wechat.helper.bean.kfweixin.SnsAccessToken;
import com.sunwayworld.cloud.module.mobile.wechat.helper.kit.PaymentKit;
import com.sunwayworld.cloud.module.mobile.wechat.helper.utils.HttpUtils;
import com.sunwayworld.cloud.module.mobile.wechat.helper.utils.RetryUtils;
import com.sunwayworld.framework.utils.StringUtils;
import java.util.HashMap;
import java.util.concurrent.Callable;

public class SnsAccessTokenApi {
    private static String url = "https://api.weixin.qq.com/sns/oauth2/access_token?appid={appid}&secret={secret}&code={code}&grant_type=authorization_code";
    private static String authorize_uri = "https://open.weixin.qq.com/connect/oauth2/authorize";
    private static String qrconnect_url = "https://open.weixin.qq.com/connect/qrconnect";

    public static String getAuthorizeURL(String appId, String redirect_uri, boolean snsapiBase) {
        return SnsAccessTokenApi.getAuthorizeURL(appId, redirect_uri, null, snsapiBase);
    }

    public static String getAuthorizeURL(String appId, String redirectUri, String state, boolean snsapiBase) {
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("appid", appId);
        params.put("response_type", "code");
        params.put("redirect_uri", redirectUri);
        if (snsapiBase) {
            params.put("scope", "snsapi_base");
        } else {
            params.put("scope", "snsapi_userinfo");
        }
        if (StringUtils.isBlank(state)) {
            params.put("state", "wx#wechat_redirect");
        } else {
            params.put("state", state.concat("#wechat_redirect"));
        }
        String para = PaymentKit.packageSign(params, false);
        return authorize_uri + "?" + para;
    }

    public static String getQrConnectURL(String appId, String redirect_uri) {
        return SnsAccessTokenApi.getQrConnectURL(appId, redirect_uri, null);
    }

    public static String getQrConnectURL(String appId, String redirect_uri, String state) {
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("appid", appId);
        params.put("response_type", "code");
        params.put("redirect_uri", redirect_uri);
        params.put("scope", "snsapi_login");
        if (StringUtils.isBlank(state)) {
            params.put("state", "wx#wechat_redirect");
        } else {
            params.put("state", state.concat("#wechat_redirect"));
        }
        String para = PaymentKit.packageSign(params, false);
        return qrconnect_url + "?" + para;
    }

    public static SnsAccessToken getSnsAccessToken(String appId, String secret, String code) {
        final String accessTokenUrl = url.replace("{appid}", appId).replace("{secret}", secret).replace("{code}", code);
        return RetryUtils.retryOnException(3, new Callable<SnsAccessToken>(){

            @Override
            public SnsAccessToken call() throws Exception {
                String json = HttpUtils.get(accessTokenUrl);
                return new SnsAccessToken(json);
            }
        });
    }
}
