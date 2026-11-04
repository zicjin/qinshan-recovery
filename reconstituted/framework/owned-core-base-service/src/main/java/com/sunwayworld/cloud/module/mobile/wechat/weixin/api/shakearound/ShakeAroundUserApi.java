/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.mobile.wechat.weixin.api.shakearound;

import com.alibaba.fastjson.JSON;
import com.sunwayworld.cloud.module.mobile.wechat.helper.bean.ApiResult;
import com.sunwayworld.cloud.module.mobile.wechat.helper.utils.HttpUtils;
import com.sunwayworld.cloud.module.mobile.wechat.weixin.api.AccessTokenApi;
import java.util.HashMap;

public class ShakeAroundUserApi {
    private static String getShakeInfoUrl = "https://api.weixin.qq.com/shakearound/user/getshakeinfo?access_token=";

    public static ApiResult getShakeInfo(String ticket) {
        return ShakeAroundUserApi.getShakeInfo(ticket, false);
    }

    public static ApiResult getShakeInfo(String ticket, boolean needPoi) {
        String url = getShakeInfoUrl + AccessTokenApi.getAccessTokenStr();
        HashMap<String, Object> data = new HashMap<String, Object>();
        data.put("ticket", ticket);
        if (needPoi) {
            data.put("need_poi", 1);
        }
        String jsonResult = HttpUtils.post(url, JSON.toJSONString(data));
        return new ApiResult(jsonResult);
    }
}
