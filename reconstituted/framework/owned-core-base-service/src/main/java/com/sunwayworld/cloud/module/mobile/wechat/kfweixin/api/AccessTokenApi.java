/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.mobile.wechat.kfweixin.api;

import com.sunwayworld.cloud.module.mobile.wechat.helper.bean.kfweixin.AccessToken;
import com.sunwayworld.cloud.module.mobile.wechat.helper.cache.IAccessTokenCache;
import com.sunwayworld.cloud.module.mobile.wechat.helper.kit.ParaMap;
import com.sunwayworld.cloud.module.mobile.wechat.helper.utils.HttpUtils;
import com.sunwayworld.cloud.module.mobile.wechat.helper.utils.RetryUtils;
import com.sunwayworld.cloud.module.mobile.wechat.kfweixin.api.ApiConfig;
import com.sunwayworld.cloud.module.mobile.wechat.kfweixin.api.ApiConfigKit;
import com.sunwayworld.framework.utils.StringUtils;
import java.util.Map;
import java.util.concurrent.Callable;

public class AccessTokenApi {
    private static String url = "https://api.weixin.qq.com/cgi-bin/token?grant_type=client_credential";
    private static AccessToken accessToken = null;

    public static void setAccessToken(AccessToken ak) {
        accessToken = ak;
    }

    public static void removeAccessToken() {
        accessToken = null;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static AccessToken getAccessToken() {
        if (accessToken != null) {
            return accessToken;
        }
        ApiConfig ac = ApiConfigKit.getApiConfig();
        AccessToken result = AccessTokenApi.getAvailableAccessToken(ac);
        if (result == null) {
            ApiConfig apiConfig = ac;
            synchronized (apiConfig) {
                result = AccessTokenApi.getAvailableAccessToken(ac);
                if (result == null) {
                    result = AccessTokenApi.refreshAccessToken(ac);
                }
            }
        }
        return result;
    }

    private static AccessToken getAvailableAccessToken(ApiConfig ac) {
        AccessToken result;
        IAccessTokenCache accessTokenCache = ApiConfigKit.getAccessTokenCache();
        String accessTokenJson = accessTokenCache.get(ac.getAppId());
        if (!StringUtils.isBlank(accessTokenJson) && (result = new AccessToken(accessTokenJson)) != null && result.isAvailable()) {
            return result;
        }
        return null;
    }

    public static String getAccessTokenStr() {
        return AccessTokenApi.getAccessToken().getAccessToken();
    }

    public static AccessToken refreshAccessToken(ApiConfig ac) {
        String appId = ac.getAppId();
        String appSecret = ac.getAppSecret();
        final Map<String, String> queryParas = ParaMap.create("appid", appId).put("secret", appSecret).getData();
        AccessToken result = RetryUtils.retryOnException(3, new Callable<AccessToken>(){

            @Override
            public AccessToken call() throws Exception {
                String json = HttpUtils.get(url, queryParas);
                return new AccessToken(json);
            }
        });
        if (null != result) {
            IAccessTokenCache accessTokenCache = ApiConfigKit.getAccessTokenCache();
            accessTokenCache.set(ac.getAppId(), result.getCacheJson());
        }
        return result;
    }
}
