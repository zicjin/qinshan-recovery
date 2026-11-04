/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.module.sys.wechat.support;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.sunwayworld.framework.cache.redis.RedisHelper;
import com.sunwayworld.framework.exception.core.ApplicationWarningException;
import com.sunwayworld.framework.http.HttpClientManager;
import com.sunwayworld.framework.utils.ConvertUtils;
import com.sunwayworld.framework.utils.ObjectUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

@Component
public class CoreWechatMPManager {
    @Value(value="${sunway.wechat.mp.appID:}")
    private String appID;
    @Value(value="${sunway.wechat.mp.appsecret:}")
    private String appsecret;
    @Value(value="${sunway.wechat.qiye.appID:}")
    private String corpID;
    @Value(value="${sunway.wechat.qiye.appsecret:}")
    private String corpsecret;
    private static final String WECHAT_MP_ACCESS_TOKEN_KEY = "mp_at";
    private static final String WECHAT_QY_ACCESS_TOKEN_KEY = "qy_at";
    private static final String WECHAT_ACCESS_TOKEN_NAME = "wechat";
    public static final String WECHAT_API_URL = "https://api.weixin.qq.com";
    public static final String WECHAT_QYAPI_URL = "https://qyapi.weixin.qq.com";
    private static HttpClientManager httpClinet = HttpClientManager.getInstance();

    public String getAccessToken() {
        String accessToken = (String)RedisHelper.get(WECHAT_ACCESS_TOKEN_NAME, WECHAT_MP_ACCESS_TOKEN_KEY);
        if (accessToken == null) {
            UriComponentsBuilder uriComponentsBuilder = UriComponentsBuilder.fromHttpUrl(WECHAT_API_URL);
            uriComponentsBuilder.path("/cgi-bin/token").queryParam("grant_type", "client_credential").queryParam("appid", this.appID).queryParam("secret", this.appsecret);
            String result = httpClinet.sendHttpGet(uriComponentsBuilder.toUriString());
            JSONObject jsonObject = JSON.parseObject(result);
            if (jsonObject.containsKey("access_token")) {
                accessToken = ConvertUtils.convert(jsonObject.get("access_token"), String.class);
                RedisHelper.put(WECHAT_ACCESS_TOKEN_NAME, WECHAT_MP_ACCESS_TOKEN_KEY, accessToken, ConvertUtils.convert(jsonObject.get("expires_in"), Integer.class));
            } else if (jsonObject.containsKey("errcode")) {
                throw new ApplicationWarningException(result, new String[0]);
            }
        }
        return accessToken;
    }

    public String getCorpAccessToken() {
        String accessToken = (String)RedisHelper.get(WECHAT_ACCESS_TOKEN_NAME, WECHAT_QY_ACCESS_TOKEN_KEY);
        if (accessToken == null) {
            UriComponentsBuilder uriComponentsBuilder = UriComponentsBuilder.fromHttpUrl(WECHAT_QYAPI_URL);
            uriComponentsBuilder.path("/cgi-bin/gettoken").queryParam("corpid", this.corpID).queryParam("corpsecret", this.corpsecret);
            String result = httpClinet.sendHttpGet(uriComponentsBuilder.toUriString());
            JSONObject jsonObject = JSON.parseObject(result);
            if (ObjectUtils.equals(jsonObject.get("errcode"), 0)) {
                accessToken = ConvertUtils.convert(jsonObject.get("access_token"), String.class);
                RedisHelper.put(WECHAT_ACCESS_TOKEN_NAME, WECHAT_QY_ACCESS_TOKEN_KEY, accessToken, ConvertUtils.convert(jsonObject.get("expires_in"), Integer.class));
            } else {
                throw new ApplicationWarningException(result, new String[0]);
            }
        }
        return accessToken;
    }
}
