/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.mobile.wechat.qyweixin.api;

import com.sunwayworld.cloud.module.mobile.wechat.helper.bean.ApiResult;
import com.sunwayworld.cloud.module.mobile.wechat.helper.kit.ParaMap;
import com.sunwayworld.cloud.module.mobile.wechat.helper.utils.HttpUtils;
import com.sunwayworld.cloud.module.mobile.wechat.qyweixin.api.AccessTokenApi;

public class AgentApi {
    private static String getUrl = "https://qyapi.weixin.qq.com/cgi-bin/agent/get";
    private static String setUrl = "https://qyapi.weixin.qq.com/cgi-bin/agent/set?access_token=";
    private static String getListUrl = "https://qyapi.weixin.qq.com/cgi-bin/agent/list?access_token=";

    public static ApiResult getAgent(String agentid) {
        ParaMap pm = ParaMap.create("access_token", AccessTokenApi.getAccessTokenStr()).put("agentid", agentid);
        String jsonResult = HttpUtils.get(getUrl, pm.getData());
        return new ApiResult(jsonResult);
    }

    public static ApiResult setAgent(String data) {
        String jsonResult = HttpUtils.post(setUrl + AccessTokenApi.getAccessTokenStr(), data);
        return new ApiResult(jsonResult);
    }

    public static ApiResult getListAgent() {
        String jsonResult = HttpUtils.get(getListUrl + AccessTokenApi.getAccessTokenStr());
        return new ApiResult(jsonResult);
    }
}
