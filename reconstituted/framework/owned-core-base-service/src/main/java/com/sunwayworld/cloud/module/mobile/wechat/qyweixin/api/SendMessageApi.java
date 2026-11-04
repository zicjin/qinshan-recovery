/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.mobile.wechat.qyweixin.api;

import com.alibaba.fastjson.JSON;
import com.sunwayworld.cloud.module.mobile.wechat.helper.bean.ApiResult;
import com.sunwayworld.cloud.module.mobile.wechat.helper.kit.ParaMap;
import com.sunwayworld.cloud.module.mobile.wechat.helper.msg.send.QiYeFileMsg;
import com.sunwayworld.cloud.module.mobile.wechat.helper.msg.send.QiYeImageMsg;
import com.sunwayworld.cloud.module.mobile.wechat.helper.msg.send.QiYeMpNewsMsg;
import com.sunwayworld.cloud.module.mobile.wechat.helper.msg.send.QiYeNewsMsg;
import com.sunwayworld.cloud.module.mobile.wechat.helper.msg.send.QiYeTextCardMsg;
import com.sunwayworld.cloud.module.mobile.wechat.helper.msg.send.QiYeTextMsg;
import com.sunwayworld.cloud.module.mobile.wechat.helper.msg.send.QiYeVideoMsg;
import com.sunwayworld.cloud.module.mobile.wechat.helper.msg.send.QiYeVoiceMsg;
import com.sunwayworld.cloud.module.mobile.wechat.helper.utils.HttpUtils;
import com.sunwayworld.cloud.module.mobile.wechat.qyweixin.api.AccessTokenApi;

public class SendMessageApi {
    private static final String url = "https://qyapi.weixin.qq.com/cgi-bin/message/send";

    public static ApiResult sendTextMsg(QiYeTextMsg text) {
        String jsonStr = JSON.toJSONString(text);
        return SendMessageApi.sendMessage(jsonStr);
    }

    public static ApiResult sendTextCardMsg(QiYeTextCardMsg text) {
        String jsonStr = JSON.toJSONString(text);
        return SendMessageApi.sendMessage(jsonStr);
    }

    public static ApiResult sendImageMsg(QiYeImageMsg image) {
        String jsonStr = JSON.toJSONString(image);
        return SendMessageApi.sendMessage(jsonStr);
    }

    public static ApiResult sendVoiceMsg(QiYeVoiceMsg voice) {
        String jsonStr = JSON.toJSONString(voice);
        return SendMessageApi.sendMessage(jsonStr);
    }

    public static ApiResult sendVideoMsg(QiYeVideoMsg video) {
        String jsonStr = JSON.toJSONString(video);
        return SendMessageApi.sendMessage(jsonStr);
    }

    public static ApiResult sendNewsMsg(QiYeNewsMsg news) {
        String jsonStr = JSON.toJSONString(news);
        return SendMessageApi.sendMessage(jsonStr);
    }

    public static ApiResult sendMpNewsMsg(QiYeMpNewsMsg mpNews) {
        String jsonStr = JSON.toJSONString(mpNews);
        return SendMessageApi.sendMessage(jsonStr);
    }

    public static ApiResult sendFileMsg(QiYeFileMsg file) {
        String jsonStr = JSON.toJSONString(file);
        return SendMessageApi.sendMessage(jsonStr);
    }

    public static ApiResult sendMessage(String jsonStr) {
        ParaMap pm = ParaMap.create("access_token", AccessTokenApi.getAccessTokenStr());
        return new ApiResult(HttpUtils.post(url, pm.getData(), jsonStr));
    }
}
