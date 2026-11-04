/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.mobile.wechat.qyweixin.api;

import com.sunwayworld.cloud.module.mobile.wechat.helper.bean.qyweixin.AccessToken;
import com.sunwayworld.cloud.module.mobile.wechat.helper.cache.IAccessTokenCache;
import com.sunwayworld.cloud.module.mobile.wechat.helper.kit.ParaMap;
import com.sunwayworld.cloud.module.mobile.wechat.helper.utils.HttpUtils;
import com.sunwayworld.cloud.module.mobile.wechat.helper.utils.RetryUtils;
import com.sunwayworld.cloud.module.mobile.wechat.qyweixin.api.ApiConfig;
import com.sunwayworld.cloud.module.mobile.wechat.qyweixin.api.ApiConfigKit;
import com.sunwayworld.framework.utils.StringUtils;
import java.util.Map;
import java.util.concurrent.Callable;

public class AccessTokenApi {
    private static String url = "https://qyapi.weixin.qq.com/cgi-bin/gettoken";

    public static AccessToken getAccessToken() {
        ApiConfig ac = ApiConfigKit.getApiConfig();
        AccessToken result = AccessTokenApi.getAvailableAccessToken(ac);
        if (result != null) {
            return result;
        }
        return AccessTokenApi.refreshAccessTokenIfNecessary(ac);
    }

    private static AccessToken getAvailableAccessToken(ApiConfig ac) {
        AccessToken result;
        IAccessTokenCache accessTokenCache = ApiConfigKit.getAccessTokenCache();
        String accessTokenJson = accessTokenCache.get(ac.getAgentId());
        if (!StringUtils.isBlank(accessTokenJson) && (result = new AccessToken(accessTokenJson)) != null && result.isAvailable()) {
            return result;
        }
        return null;
    }

    public static String getAccessTokenStr() {
        return AccessTokenApi.getAccessToken().getAccessToken();
    }

    private static synchronized AccessToken refreshAccessTokenIfNecessary(ApiConfig ac) {
        AccessToken result = AccessTokenApi.getAvailableAccessToken(ac);
        if (result != null) {
            return result;
        }
        return AccessTokenApi.refreshAccessToken(ac);
    }

    public static AccessToken refreshAccessToken() {
        return AccessTokenApi.refreshAccessToken(ApiConfigKit.getApiConfig());
    }

    public static AccessToken refreshAccessToken(ApiConfig ac) {
        String corpid = ac.getCorpId();
        String corpsecret = ac.getCorpSecret();
        final Map<String, String> queryParas = ParaMap.create("corpid", corpid).put("corpsecret", corpsecret).getData();
        AccessToken result = RetryUtils.retryOnException(3, new Callable<AccessToken>(){

            @Override
            public AccessToken call() throws Exception {
                String json = HttpUtils.get(url, queryParas);
                return new AccessToken(json);
            }
        });
        if (null != result) {
            IAccessTokenCache accessTokenCache = ApiConfigKit.getAccessTokenCache();
            accessTokenCache.set(ac.getAgentId(), result.getCacheJson());
        }
        return result;
    }
}
