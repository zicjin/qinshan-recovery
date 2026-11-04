/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.mobile.wechat.qyweixin.api;

import com.sunwayworld.cloud.module.mobile.wechat.helper.bean.ApiResult;
import com.sunwayworld.cloud.module.mobile.wechat.helper.kit.ParaMap;
import com.sunwayworld.cloud.module.mobile.wechat.helper.utils.HttpUtils;
import com.sunwayworld.cloud.module.mobile.wechat.qyweixin.api.AccessTokenApi;

public class MenuApi {
    private static String getMenu = "https://qyapi.weixin.qq.com/cgi-bin/menu/get";
    private static String createMenu = "https://qyapi.weixin.qq.com/cgi-bin/menu/create";
    private static String deleteMenu = "https://qyapi.weixin.qq.com/cgi-bin/menu/delete";

    public static ApiResult getMenu(String agentid) {
        ParaMap pm = ParaMap.create("access_token", AccessTokenApi.getAccessTokenStr()).put("agentid", agentid);
        String jsonResult = HttpUtils.get(getMenu, pm.getData());
        return new ApiResult(jsonResult);
    }

    public static ApiResult createMenu(String jsonStr, String agentid) {
        ParaMap pm = ParaMap.create("access_token", AccessTokenApi.getAccessTokenStr()).put("agentid", agentid);
        String jsonResult = HttpUtils.post(createMenu, pm.getData(), jsonStr);
        return new ApiResult(jsonResult);
    }

    public static ApiResult deleteMenu(String agentid) {
        ParaMap pm = ParaMap.create("access_token", AccessTokenApi.getAccessTokenStr()).put("agentid", agentid);
        String jsonResult = HttpUtils.get(deleteMenu, pm.getData());
        return new ApiResult(jsonResult);
    }
}
