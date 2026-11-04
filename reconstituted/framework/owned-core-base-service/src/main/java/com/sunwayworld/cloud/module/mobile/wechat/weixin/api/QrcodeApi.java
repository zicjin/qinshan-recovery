/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.mobile.wechat.weixin.api;

import com.alibaba.fastjson.JSON;
import com.sunwayworld.cloud.module.mobile.wechat.helper.bean.ApiResult;
import com.sunwayworld.cloud.module.mobile.wechat.helper.utils.HttpUtils;
import com.sunwayworld.cloud.module.mobile.wechat.weixin.api.AccessTokenApi;
import java.util.HashMap;

public class QrcodeApi {
    private static String apiUrl = "https://api.weixin.qq.com/cgi-bin/qrcode/create?access_token=";
    private static String showQrcodeUrl = "https://mp.weixin.qq.com/cgi-bin/showqrcode?ticket=";

    public static ApiResult create(String jsonStr) {
        String jsonResult = HttpUtils.post(apiUrl + AccessTokenApi.getAccessTokenStr(), jsonStr);
        return new ApiResult(jsonResult);
    }

    public static ApiResult createTemporary(int expireSeconds, int sceneId) {
        HashMap<String, Object> params = new HashMap<String, Object>();
        params.put("expire_seconds", expireSeconds);
        params.put("action_name", "QR_SCENE");
        HashMap actionInfo = new HashMap();
        HashMap<String, Integer> scene = new HashMap<String, Integer>();
        scene.put("scene_id", sceneId);
        actionInfo.put("scene", scene);
        params.put("action_info", actionInfo);
        return QrcodeApi.create(JSON.toJSONString(params));
    }

    public static ApiResult createTemporary(int expireSeconds, String sceneStr) {
        HashMap<String, Object> params = new HashMap<String, Object>();
        params.put("expire_seconds", expireSeconds);
        params.put("action_name", "QR_STR_SCENE");
        HashMap actionInfo = new HashMap();
        HashMap<String, String> scene = new HashMap<String, String>();
        scene.put("scene_str", sceneStr);
        actionInfo.put("scene", scene);
        params.put("action_info", actionInfo);
        return QrcodeApi.create(JSON.toJSONString(params));
    }

    public static ApiResult createPermanent(int sceneId) {
        HashMap<String, Object> params = new HashMap<String, Object>();
        params.put("action_name", "QR_LIMIT_SCENE");
        HashMap actionInfo = new HashMap();
        HashMap<String, Integer> scene = new HashMap<String, Integer>();
        scene.put("scene_id", sceneId);
        actionInfo.put("scene", scene);
        params.put("action_info", actionInfo);
        return QrcodeApi.create(JSON.toJSONString(params));
    }

    public static ApiResult createPermanent(String sceneStr) {
        HashMap<String, Object> params = new HashMap<String, Object>();
        params.put("action_name", "QR_LIMIT_STR_SCENE");
        HashMap actionInfo = new HashMap();
        HashMap<String, String> scene = new HashMap<String, String>();
        scene.put("scene_str", sceneStr);
        actionInfo.put("scene", scene);
        params.put("action_info", actionInfo);
        return QrcodeApi.create(JSON.toJSONString(params));
    }

    public static String getShowQrcodeUrl(String ticket) {
        return showQrcodeUrl + ticket;
    }
}
