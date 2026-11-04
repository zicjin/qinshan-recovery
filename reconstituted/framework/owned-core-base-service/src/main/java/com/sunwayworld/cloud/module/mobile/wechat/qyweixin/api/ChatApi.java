/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.mobile.wechat.qyweixin.api;

import com.sunwayworld.cloud.module.mobile.wechat.helper.bean.ApiResult;
import com.sunwayworld.cloud.module.mobile.wechat.helper.kit.ParaMap;
import com.sunwayworld.cloud.module.mobile.wechat.helper.utils.HttpUtils;
import com.sunwayworld.cloud.module.mobile.wechat.qyweixin.api.AccessTokenApi;

public class ChatApi {
    private static String createUrl = "https://qyapi.weixin.qq.com/cgi-bin/chat/create?access_token=ACCESS_TOKEN";
    private static String getUrl = "https://qyapi.weixin.qq.com/cgi-bin/chat/get";
    private static String updateUrl = "https://qyapi.weixin.qq.com/cgi-bin/chat/update?access_token=ACCESS_TOKEN";
    private static String quitUrl = "https://qyapi.weixin.qq.com/cgi-bin/chat/quit?access_token=ACCESS_TOKEN";
    private static String clearNotityUrl = "https://qyapi.weixin.qq.com/cgi-bin/chat/clearnotify?access_token=ACCESS_TOKEN";
    private static String sendUrl = "https://qyapi.weixin.qq.com/cgi-bin/chat/send?access_token=ACCESS_TOKEN";
    private static String setmuteUrl = "https://qyapi.weixin.qq.com/cgi-bin/chat/setmute?access_token=ACCESS_TOKEN";

    public static ApiResult Chat(ChatUrl chatUrl, String data) {
        String url = null;
        String urlName = chatUrl.name();
        if (urlName.equals("createUrl")) {
            url = createUrl;
        } else if (urlName.equals("updateUrl")) {
            url = updateUrl;
        } else if (urlName.equals("quitUrl")) {
            url = quitUrl;
        } else if (urlName.equals("clearNotityUrl")) {
            url = clearNotityUrl;
        } else if (urlName.equals("sendUrl")) {
            url = sendUrl;
        } else if (urlName.equals("setmuteUrl")) {
            url = setmuteUrl;
        }
        String jsonResult = HttpUtils.post(url + AccessTokenApi.getAccessTokenStr(), data);
        return new ApiResult(jsonResult);
    }

    public static ApiResult getChat(String chatid, String data) {
        ParaMap pm = ParaMap.create("access_token", AccessTokenApi.getAccessTokenStr()).put("chatid", chatid);
        String jsonResult = HttpUtils.post(getUrl, pm.getData(), data);
        return new ApiResult(jsonResult);
    }

    public static enum ChatUrl {
        createUrl,
        updateUrl,
        quitUrl,
        clearNotityUrl,
        sendUrl,
        setmuteUrl;

    }
}
