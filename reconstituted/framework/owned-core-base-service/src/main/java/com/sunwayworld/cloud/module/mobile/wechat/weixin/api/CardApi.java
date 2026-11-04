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

public class CardApi {
    private static String cardCreateUrl = "https://api.weixin.qq.com/card/create?access_token=";
    private static String createQrcodeCard = "https://api.weixin.qq.com/card/qrcode/create?access_token=";
    private static String createLandingPageCard = "https://api.weixin.qq.com/card/landingpage/create?access_token=";
    private static String gethtmlMpnews = "https://api.weixin.qq.com/card/mpnews/gethtml?access_token=";
    private static String setTestWhiteList = "https://api.weixin.qq.com/card/testwhitelist/set?access_token=";
    private static String setPaycell = "https://api.weixin.qq.com/card/testwhitelist/set?access_token=";
    private static String setSelfconsumecell = "https://api.weixin.qq.com/card/selfconsumecell/set?access_token=";
    private static String getUserCardList = "https://api.weixin.qq.com/card/user/getcardlist?access_token=";
    private static String getCard = "https://api.weixin.qq.com/card/get?access_token=";
    private static String getBatch = "https://api.weixin.qq.com/card/batchget?access_token=";
    private static String update = "https://api.weixin.qq.com/card/update?access_token=";
    private static String modifystock = "https://api.weixin.qq.com/card/modifystock?access_token=";
    private static String delete = "https://api.weixin.qq.com/card/delete?access_token=";
    private static String unavailable = "https://api.weixin.qq.com/card/code/unavailable?access_token=";

    public static ApiResult create(String jsonStr) {
        String jsonResult = HttpUtils.post(cardCreateUrl + AccessTokenApi.getAccessTokenStr(), jsonStr);
        return new ApiResult(jsonResult);
    }

    public static ApiResult createQrcode(String jsonStr) {
        String jsonResult = HttpUtils.post(createQrcodeCard + AccessTokenApi.getAccessTokenStr(), jsonStr);
        return new ApiResult(jsonResult);
    }

    public static ApiResult createLandingPage(String jsonStr) {
        String jsonResult = HttpUtils.post(createLandingPageCard + AccessTokenApi.getAccessTokenStr(), jsonStr);
        return new ApiResult(jsonResult);
    }

    public static ApiResult gethtmlMpnews(String cardId) {
        Kv data = Kv.create();
        if (!StringUtils.isBlank(cardId)) {
            data.set("card_id", cardId);
        }
        String jsonResult = HttpUtils.post(gethtmlMpnews + AccessTokenApi.getAccessTokenStr(), JSON.toJSONString(data));
        return new ApiResult(jsonResult);
    }

    public static ApiResult setTestWhiteList(String jsonStr) {
        String jsonResult = HttpUtils.post(setTestWhiteList + AccessTokenApi.getAccessTokenStr(), jsonStr);
        return new ApiResult(jsonResult);
    }

    public static ApiResult setPaycell(String cardId, boolean isOpen) {
        Kv data = Kv.by("card_id", cardId).set("is_open", isOpen);
        String jsonResult = HttpUtils.post(setPaycell + AccessTokenApi.getAccessTokenStr(), JSON.toJSONString(data));
        return new ApiResult(jsonResult);
    }

    public static ApiResult setSelfconsumecell(String cardId, boolean isOpen) {
        return CardApi.setSelfconsumecell(cardId, isOpen, false, false);
    }

    public static ApiResult setSelfconsumecell(String cardId, boolean isOpen, boolean needVerifyCod, boolean needRemarkAmount) {
        Kv data = Kv.by("card_id", cardId).set("is_open", isOpen).set("need_verify_cod", needVerifyCod).set("need_remark_amount", needRemarkAmount);
        String jsonResult = HttpUtils.post(setSelfconsumecell + AccessTokenApi.getAccessTokenStr(), JSON.toJSONString(data));
        return new ApiResult(jsonResult);
    }

    public static ApiResult getUserCardList(String openid) {
        return CardApi.getUserCardList(openid, null);
    }

    public static ApiResult getUserCardList(String openid, String cardId) {
        Kv data = Kv.by("openid", openid);
        if (!StringUtils.isBlank(cardId)) {
            data.set("card_id", cardId);
        }
        String jsonResult = HttpUtils.post(getUserCardList + AccessTokenApi.getAccessTokenStr(), JSON.toJSONString(data));
        return new ApiResult(jsonResult);
    }

    public static ApiResult get(String cardId) {
        Kv data = Kv.by("card_id", cardId);
        String jsonResult = HttpUtils.post(getCard + AccessTokenApi.getAccessTokenStr(), JSON.toJSONString(data));
        return new ApiResult(jsonResult);
    }

    public static ApiResult getBatch(int offset, int count) {
        return CardApi.getBatch(offset, count, null);
    }

    public static ApiResult getBatch(int offset, int count, List<String> statusList) {
        Kv data = Kv.by("offset", offset).set("count", count);
        if (statusList != null && !statusList.isEmpty()) {
            data.set("status_list", statusList);
        }
        String jsonResult = HttpUtils.post(getBatch + AccessTokenApi.getAccessTokenStr(), JSON.toJSONString(data));
        return new ApiResult(jsonResult);
    }

    public static ApiResult update(String jsonStr) {
        String jsonResult = HttpUtils.post(update + AccessTokenApi.getAccessTokenStr(), jsonStr);
        return new ApiResult(jsonResult);
    }

    public static ApiResult modifystock(String cardId, int increasetockValue, int reduceStockValue) {
        Kv data = Kv.by("card_id", cardId);
        if (increasetockValue >= 0) {
            data.set("increase_stock_value", increasetockValue);
        }
        if (reduceStockValue >= 0) {
            data.set("reduce_stock_value", reduceStockValue);
        }
        String jsonResult = HttpUtils.post(modifystock + AccessTokenApi.getAccessTokenStr(), JSON.toJSONString(data));
        return new ApiResult(jsonResult);
    }

    public static ApiResult delete(String cardId) {
        Kv data = Kv.by("card_id", cardId);
        String jsonResult = HttpUtils.post(delete + AccessTokenApi.getAccessTokenStr(), JSON.toJSONString(data));
        return new ApiResult(jsonResult);
    }

    public static ApiResult unavailableByCode(String code, String reason) {
        Kv data = Kv.by("code", code).set("reason", reason);
        String jsonResult = HttpUtils.post(unavailable + AccessTokenApi.getAccessTokenStr(), JSON.toJSONString(data));
        return new ApiResult(jsonResult);
    }

    public static ApiResult unavailableByCard(String cardId, String reason) {
        Kv data = Kv.by("card_id", cardId).set("reason", reason);
        String jsonResult = HttpUtils.post(unavailable + AccessTokenApi.getAccessTokenStr(), JSON.toJSONString(data));
        return new ApiResult(jsonResult);
    }
}
