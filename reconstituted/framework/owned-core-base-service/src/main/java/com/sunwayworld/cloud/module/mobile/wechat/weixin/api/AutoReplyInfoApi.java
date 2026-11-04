/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.mobile.wechat.weixin.api;

import com.sunwayworld.cloud.module.mobile.wechat.helper.bean.ApiResult;
import com.sunwayworld.cloud.module.mobile.wechat.helper.utils.HttpUtils;
import com.sunwayworld.cloud.module.mobile.wechat.weixin.api.AccessTokenApi;

public class AutoReplyInfoApi {
    private static String getCurrentAutoreplyInfoUrl = "https://api.weixin.qq.com/cgi-bin/get_current_autoreply_info?access_token=";

    public static ApiResult getCurrent() {
        String jsonResult = HttpUtils.get(getCurrentAutoreplyInfoUrl + AccessTokenApi.getAccessTokenStr());
        return new ApiResult(jsonResult);
    }
}
