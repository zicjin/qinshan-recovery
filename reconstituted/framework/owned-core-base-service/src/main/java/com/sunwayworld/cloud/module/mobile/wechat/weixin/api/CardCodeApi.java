/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.mobile.wechat.weixin.api;

import com.alibaba.fastjson.JSON;
import com.sunwayworld.cloud.module.mobile.wechat.helper.bean.ApiResult;
import com.sunwayworld.cloud.module.mobile.wechat.helper.utils.HttpUtils;
import com.sunwayworld.cloud.module.mobile.wechat.helper.utils.Kv;
import com.sunwayworld.cloud.module.mobile.wechat.weixin.api.AccessTokenApi;
import com.sunwayworld.framework.utils.StringUtils;
import java.util.List;

public class CardCodeApi {
    private static String getCodeUrl = "https://api.weixin.qq.com/card/code/get?access_token=";
    private static String consumeCode = "https://api.weixin.qq.com/card/code/consume?access_token=";
    private static String decryptCode = "https://api.weixin.qq.com/card/code/decrypt?access_token=";
    private static String setDeposit = "http://api.weixin.qq.com/card/code/deposit?access_token=";
    private static String getDepositCount = "http://api.weixin.qq.com/card/code/getdepositcount?access_token=";
    private static String checkCode = "http://api.weixin.qq.com/card/code/checkcode?access_token=";
    private static String update = "https://api.weixin.qq.com/card/code/update?access_token=";
    private static String mark = "https://api.weixin.qq.com/card/code/mark?access_token=";

    public static ApiResult get(String code, String cardId, boolean checkConsume) {
        Kv data = Kv.by("code", code);
        if (!StringUtils.isBlank(cardId)) {
            data.set("card_id", cardId);
        }
        data.set("check_consume", checkConsume);
        String jsonResult = HttpUtils.post(getCodeUrl + AccessTokenApi.getAccessTokenStr(), JSON.toJSONString(data));
        return new ApiResult(jsonResult);
    }

    public static ApiResult consume(String code) {
        return CardCodeApi.consume(code, null);
    }

    public static ApiResult consume(String code, String cardId) {
        Kv data = Kv.by("code", code);
        if (!StringUtils.isBlank(cardId)) {
            data.set("card_id", cardId);
        }
        String jsonResult = HttpUtils.post(consumeCode + AccessTokenApi.getAccessTokenStr(), JSON.toJSONString(data));
        return new ApiResult(jsonResult);
    }

    public static ApiResult consumeOnline(String code, String openid) {
        Kv data = Kv.by("code", code).set("openid", openid);
        String jsonResult = HttpUtils.post(consumeCode + AccessTokenApi.getAccessTokenStr(), JSON.toJSONString(data));
        return new ApiResult(jsonResult);
    }

    public static ApiResult decrypt(String encryptCode) {
        Kv data = Kv.by("encrypt_code", encryptCode);
        String jsonResult = HttpUtils.post(decryptCode + AccessTokenApi.getAccessTokenStr(), JSON.toJSONString(data));
        return new ApiResult(jsonResult);
    }

    public static ApiResult setDeposit(String cardId, List<String> codeList) {
        Kv data = Kv.by("card_id", cardId).set("code", codeList);
        String jsonResult = HttpUtils.post(setDeposit + AccessTokenApi.getAccessTokenStr(), JSON.toJSONString(data));
        return new ApiResult(jsonResult);
    }

    public static ApiResult getDepositCount(String cardId) {
        Kv data = Kv.by("card_id", cardId);
        String jsonResult = HttpUtils.post(getDepositCount + AccessTokenApi.getAccessTokenStr(), JSON.toJSONString(data));
        return new ApiResult(jsonResult);
    }

    public static ApiResult checkCode(String cardId, List<String> codeList) {
        Kv data = Kv.by("card_id", cardId).set("code", codeList);
        String jsonResult = HttpUtils.post(checkCode + AccessTokenApi.getAccessTokenStr(), JSON.toJSONString(data));
        return new ApiResult(jsonResult);
    }

    public static ApiResult update(String code, String newCode) {
        return CardCodeApi.update(null, code, newCode);
    }

    public static ApiResult update(String cardId, String code, String newCode) {
        Kv data = Kv.by("code", code).set("new_code", newCode);
        if (!StringUtils.isBlank(cardId)) {
            data.set("card_id", cardId);
        }
        String jsonResult = HttpUtils.post(update + AccessTokenApi.getAccessTokenStr(), JSON.toJSONString(data));
        return new ApiResult(jsonResult);
    }

    public static ApiResult markCode(String code, String cardId, String openid, boolean isMark) {
        Kv data = Kv.by("code", code).set("card_id", cardId).set("openid", openid).set("is_mark", isMark);
        String jsonResult = HttpUtils.post(mark + AccessTokenApi.getAccessTokenStr(), JSON.toJSONString(data));
        return new ApiResult(jsonResult);
    }
}
