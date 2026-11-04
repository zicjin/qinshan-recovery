/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.mobile.wechat.weixin.api;

import com.alibaba.fastjson.JSON;
import com.sunwayworld.cloud.module.mobile.wechat.helper.bean.ApiResult;
import com.sunwayworld.cloud.module.mobile.wechat.helper.utils.HttpUtils;
import com.sunwayworld.cloud.module.mobile.wechat.weixin.api.AccessTokenApi;
import java.util.HashMap;

public class MessageApi {
    private static String sendAllUrl = "https://api.weixin.qq.com/cgi-bin/message/mass/sendall?access_token=";
    private static String sendUrl = "https://api.weixin.qq.com/cgi-bin/message/mass/send?access_token=";
    private static String previewUrl = "https://api.weixin.qq.com/cgi-bin/message/mass/preview?access_token=";
    private static String getUrl = "https://api.weixin.qq.com/cgi-bin/message/mass/get?access_token=";
    private static String deleteUrl = "https://api.weixin.qq.com/cgi-bin/message/mass/delete?access_token=";

    private static ApiResult post(String baseUrl, String jsonStr) {
        String url = baseUrl + AccessTokenApi.getAccessTokenStr();
        String jsonResult = HttpUtils.post(url, jsonStr);
        return new ApiResult(jsonResult);
    }

    public static ApiResult sendAll(String jsonStr) {
        return MessageApi.post(sendAllUrl, jsonStr);
    }

    public static ApiResult send(String jsonStr) {
        return MessageApi.post(sendUrl, jsonStr);
    }

    public static ApiResult preview(String jsonStr) {
        return MessageApi.post(previewUrl, jsonStr);
    }

    public static ApiResult get(String msgId) {
        HashMap<String, String> mapData = new HashMap<String, String>();
        mapData.put("msg_id", msgId);
        return MessageApi.post(getUrl, JSON.toJSONString(mapData));
    }

    public static ApiResult delete(String msgId) {
        HashMap<String, String> mapData = new HashMap<String, String>();
        mapData.put("msg_id", msgId);
        return MessageApi.post(deleteUrl, JSON.toJSONString(mapData));
    }
}
