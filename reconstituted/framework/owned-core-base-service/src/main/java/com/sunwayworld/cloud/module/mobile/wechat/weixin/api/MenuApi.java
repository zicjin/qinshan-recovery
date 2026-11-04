/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.mobile.wechat.weixin.api;

import com.alibaba.fastjson.JSON;
import com.sunwayworld.cloud.module.mobile.wechat.helper.bean.ApiResult;
import com.sunwayworld.cloud.module.mobile.wechat.helper.utils.HttpUtils;
import com.sunwayworld.cloud.module.mobile.wechat.weixin.api.AccessTokenApi;
import java.util.HashMap;

public class MenuApi {
    private static String getMenu = "https://api.weixin.qq.com/cgi-bin/menu/get?access_token=";
    private static String createMenu = "https://api.weixin.qq.com/cgi-bin/menu/create?access_token=";
    private static String deleteMenuUrl = "https://api.weixin.qq.com/cgi-bin/menu/delete?access_token=";
    private static String addConditionalUrl = "https://api.weixin.qq.com/cgi-bin/menu/addconditional?access_token=";
    private static String delConditionalUrl = "https://api.weixin.qq.com/cgi-bin/menu/delconditional?access_token=";
    private static String tryMatchUrl = "https://api.weixin.qq.com/cgi-bin/menu/trymatch?access_token=";
    private static String getCurrentSelfMenuInfoUrl = "https://api.weixin.qq.com/cgi-bin/get_current_selfmenu_info?access_token=";

    public static ApiResult getMenu() {
        String jsonResult = HttpUtils.get(getMenu + AccessTokenApi.getAccessTokenStr());
        return new ApiResult(jsonResult);
    }

    public static ApiResult createMenu(String jsonStr) {
        String jsonResult = HttpUtils.post(createMenu + AccessTokenApi.getAccessTokenStr(), jsonStr);
        return new ApiResult(jsonResult);
    }

    public static ApiResult deleteMenu() {
        String jsonResult = HttpUtils.get(deleteMenuUrl + AccessTokenApi.getAccessTokenStr());
        return new ApiResult(jsonResult);
    }

    public static ApiResult addConditional(String jsonStr) {
        String jsonResult = HttpUtils.post(addConditionalUrl + AccessTokenApi.getAccessTokenStr(), jsonStr);
        return new ApiResult(jsonResult);
    }

    public static ApiResult delConditional(String menuid) {
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("menuid", menuid);
        String url = delConditionalUrl + AccessTokenApi.getAccessTokenStr();
        String jsonResult = HttpUtils.post(url, JSON.toJSONString(params));
        return new ApiResult(jsonResult);
    }

    public static ApiResult tryMatch(String userId) {
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("user_id", userId);
        String url = tryMatchUrl + AccessTokenApi.getAccessTokenStr();
        String jsonResult = HttpUtils.post(url, JSON.toJSONString(params));
        return new ApiResult(jsonResult);
    }

    public static ApiResult getCurrentSelfMenuInfo() {
        String jsonResult = HttpUtils.get(getCurrentSelfMenuInfoUrl + AccessTokenApi.getAccessTokenStr());
        return new ApiResult(jsonResult);
    }
}
