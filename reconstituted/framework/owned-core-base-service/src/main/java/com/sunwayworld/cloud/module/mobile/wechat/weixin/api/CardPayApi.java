/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.mobile.wechat.weixin.api;

import com.alibaba.fastjson.JSON;
import com.sunwayworld.cloud.module.mobile.wechat.helper.bean.ApiResult;
import com.sunwayworld.cloud.module.mobile.wechat.helper.utils.HttpUtils;
import com.sunwayworld.cloud.module.mobile.wechat.helper.utils.Kv;
import com.sunwayworld.cloud.module.mobile.wechat.weixin.api.AccessTokenApi;

public class CardPayApi {
    private static String activateUrl = "https://api.weixin.qq.com/card/pay/activate?access_token=";
    private static String getPayPriceUrl = "https://api.weixin.qq.com/card/pay/getpayprice?access_token=";
    private static String getCoinsInfoUrl = "https://api.weixin.qq.com/card/pay/getcoinsinfo?access_token=";
    private static String confirmUrl = "https://api.weixin.qq.com/card/pay/confirm?access_token=";
    private static String rechargeUrl = "https://api.weixin.qq.com/card/pay/recharge?access_token=";
    private static String getOrderUrl = "https://api.weixin.qq.com/card/pay/getorder?access_token=";
    private static String getOrderListUrl = "https://api.weixin.qq.com/card/pay/getorderlist?access_token=";

    public static ApiResult activate() {
        String jsonResult = HttpUtils.get(activateUrl + AccessTokenApi.getAccessTokenStr());
        return new ApiResult(jsonResult);
    }

    public static ApiResult getPayPrice(String cardId, int quantity) {
        Kv data = Kv.by("card_id", cardId).set("quantity", quantity);
        String jsonResult = HttpUtils.post(getPayPriceUrl + AccessTokenApi.getAccessTokenStr(), JSON.toJSONString(data));
        return new ApiResult(jsonResult);
    }

    public static ApiResult getCoinsInfo() {
        String jsonResult = HttpUtils.get(getCoinsInfoUrl + AccessTokenApi.getAccessTokenStr());
        return new ApiResult(jsonResult);
    }

    public static ApiResult confirm(String cardId, int quantity, String orderId) {
        Kv data = Kv.by("card_id", cardId).set("quantity", quantity).set("order_id", orderId);
        String jsonResult = HttpUtils.post(confirmUrl + AccessTokenApi.getAccessTokenStr(), JSON.toJSONString(data));
        return new ApiResult(jsonResult);
    }

    public static ApiResult recharge(int coinCount) {
        Kv data = Kv.by("coin_count", coinCount);
        String jsonResult = HttpUtils.post(rechargeUrl + AccessTokenApi.getAccessTokenStr(), JSON.toJSONString(data));
        return new ApiResult(jsonResult);
    }

    public static ApiResult getOrder(String orderId) {
        Kv data = Kv.by("order_id", orderId);
        String jsonResult = HttpUtils.post(getOrderUrl + AccessTokenApi.getAccessTokenStr(), JSON.toJSONString(data));
        return new ApiResult(jsonResult);
    }

    public static ApiResult getOrderList(String jsonStr) {
        String jsonResult = HttpUtils.post(getOrderListUrl + AccessTokenApi.getAccessTokenStr(), jsonStr);
        return new ApiResult(jsonResult);
    }
}
