/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.mobile.wechat.weixin.api;

import com.sunwayworld.cloud.module.mobile.wechat.helper.bean.ApiResult;
import com.sunwayworld.cloud.module.mobile.wechat.helper.utils.HttpUtils;
import com.sunwayworld.cloud.module.mobile.wechat.weixin.api.AccessTokenApi;

public class SemanticApi {
    private static String semanticUrl = "https://api.weixin.qq.com/semantic/semproxy/search?access_token=";

    public static ApiResult search(String jsonStr) {
        String url = semanticUrl + AccessTokenApi.getAccessTokenStr();
        String jsonResult = HttpUtils.post(url, jsonStr);
        return new ApiResult(jsonResult);
    }
}
