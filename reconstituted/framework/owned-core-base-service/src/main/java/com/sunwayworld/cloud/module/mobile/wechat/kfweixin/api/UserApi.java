/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.mobile.wechat.kfweixin.api;

import com.sunwayworld.cloud.module.mobile.wechat.helper.bean.ApiResult;
import com.sunwayworld.cloud.module.mobile.wechat.helper.kit.ParaMap;
import com.sunwayworld.cloud.module.mobile.wechat.helper.utils.HttpUtils;
import com.sunwayworld.cloud.module.mobile.wechat.kfweixin.api.AccessTokenApi;

public class UserApi {
    private static String getUserInfo = "https://api.weixin.qq.com/sns/userinfo";

    public static ApiResult getUserInfo(String openId) {
        ParaMap pm = ParaMap.create("access_token", AccessTokenApi.getAccessTokenStr()).put("openid", openId).put("lang", "zh_CN");
        return new ApiResult(HttpUtils.get(getUserInfo, pm.getData()));
    }
}
