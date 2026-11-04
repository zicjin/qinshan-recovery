/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.mobile.wechat.weixin.api;

import com.alibaba.fastjson.JSON;
import com.sunwayworld.cloud.module.mobile.wechat.helper.bean.ApiResult;
import com.sunwayworld.cloud.module.mobile.wechat.helper.utils.HttpUtils;
import com.sunwayworld.cloud.module.mobile.wechat.helper.utils.Kv;
import com.sunwayworld.cloud.module.mobile.wechat.weixin.api.AccessTokenApi;

public class MemberCardApi {
    private static String activateUrl = "https://api.weixin.qq.com/card/membercard/activate?access_token=";
    private static String setActivateUserFormUrl = "https://api.weixin.qq.com/card/membercard/activateuserform/set?access_token=";
    private static String getUserInfoUrl = "https://api.weixin.qq.com/card/membercard/userinfo/get?access_token=";
    private static String getActivateTempInfoUrl = "https://api.weixin.qq.com/card/membercard/activatetempinfo/get?access_token=";
    private static String updateUserUrl = "https://api.weixin.qq.com/card/membercard/updateuser?access_token=";

    public static ApiResult activate(String jsonStr) {
        String jsonResult = HttpUtils.post(activateUrl + AccessTokenApi.getAccessTokenStr(), jsonStr);
        return new ApiResult(jsonResult);
    }

    public static ApiResult setActivateUserForm(String jsonStr) {
        String jsonResult = HttpUtils.post(setActivateUserFormUrl + AccessTokenApi.getAccessTokenStr(), jsonStr);
        return new ApiResult(jsonResult);
    }

    public static ApiResult getUserInfo(String cardId, String code) {
        Kv data = Kv.by("card_id", cardId).set("code", code);
        String jsonResult = HttpUtils.post(getUserInfoUrl + AccessTokenApi.getAccessTokenStr(), JSON.toJSONString(data));
        return new ApiResult(jsonResult);
    }

    public static ApiResult getActivateTempInfo(String activaTeicket) {
        Kv data = Kv.by("activate_ticket", activaTeicket);
        String jsonResult = HttpUtils.post(getActivateTempInfoUrl + AccessTokenApi.getAccessTokenStr(), JSON.toJSONString(data));
        return new ApiResult(jsonResult);
    }

    public static ApiResult updateUser(String jsonStr) {
        String jsonResult = HttpUtils.post(updateUserUrl + AccessTokenApi.getAccessTokenStr(), jsonStr);
        return new ApiResult(jsonResult);
    }
}
