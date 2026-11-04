/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.mobile.wechat.weixin.api;

import com.alibaba.fastjson.JSON;
import com.sunwayworld.cloud.module.mobile.wechat.helper.bean.ApiResult;
import com.sunwayworld.cloud.module.mobile.wechat.helper.utils.HttpUtils;
import com.sunwayworld.cloud.module.mobile.wechat.weixin.api.AccessTokenApi;
import java.util.HashMap;

public class ShorturlApi {
    private static String apiUrl = "https://api.weixin.qq.com/cgi-bin/shorturl?access_token=";

    public static ApiResult getShorturl(String jsonStr) {
        String jsonResult = HttpUtils.post(apiUrl + AccessTokenApi.getAccessTokenStr(), jsonStr);
        return new ApiResult(jsonResult);
    }

    public static ApiResult getShortUrl(String longUrl) {
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("action", "long2short");
        params.put("long_url", longUrl);
        return ShorturlApi.getShorturl(JSON.toJSONString(params));
    }
}
