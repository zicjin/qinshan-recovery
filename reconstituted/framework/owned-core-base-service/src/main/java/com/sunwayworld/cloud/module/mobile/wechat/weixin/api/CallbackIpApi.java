/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.mobile.wechat.weixin.api;

import com.sunwayworld.cloud.module.mobile.wechat.helper.bean.ApiResult;
import com.sunwayworld.cloud.module.mobile.wechat.helper.utils.HttpUtils;
import com.sunwayworld.cloud.module.mobile.wechat.weixin.api.AccessTokenApi;

public class CallbackIpApi {
    private static String apiUrl = "https://api.weixin.qq.com/cgi-bin/getcallbackip?access_token=";

    public static ApiResult getCallbackIp() {
        String jsonResult = HttpUtils.get(apiUrl + AccessTokenApi.getAccessTokenStr());
        return new ApiResult(jsonResult);
    }
}
