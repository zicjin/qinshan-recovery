/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.mobile.wechat.weixin.iot.api;

import com.alibaba.fastjson.JSON;
import com.sunwayworld.cloud.module.mobile.wechat.helper.bean.ApiResult;
import com.sunwayworld.cloud.module.mobile.wechat.helper.utils.HttpUtils;
import com.sunwayworld.cloud.module.mobile.wechat.helper.utils.Kv;
import com.sunwayworld.cloud.module.mobile.wechat.weixin.api.AccessTokenApi;
import com.sunwayworld.cloud.module.mobile.wechat.weixin.iot.api.DeviceAuth;
import java.util.List;

public class DeviceApi {
    private static final String TransMsgUrl = "https://api.weixin.qq.com/device/transmsg?access_token=ACCESS_TOKEN";
    private static final String AuthorizeUrl = "https://api.weixin.qq.com/device/authorize_device?access_token=ACCESS_TOKEN";
    private static final String CreateQrcode = "https://api.weixin.qq.com/device/create_qrcode?access_token=ACCESS_TOKEN";
    private static final String CreateQrcodeNew = "https://api.weixin.qq.com/device/getqrcode?access_token=ACCESS_TOKEN&product_id=PRODUCT_ID";
    private static final String GetStatUrl = "https://api.weixin.qq.com/device/get_stat?access_token=ACCESS_TOKEN&device_id=DEVICE_ID";
    private static final String VerifyQrcodeUrl = "https://api.weixin.qq.com/device/verify_qrcode?access_token=ACCESS_TOKEN";
    private static final String GetOpenidUrl = "https://api.weixin.qq.com/device/get_openid?access_token=ACCESS_TOKEN&device_type=DEVICE_TYPE&device_id=DEVICE_ID";
    private static final String bindUrl = "https://api.weixin.qq.com/device/bind?access_token=ACCESS_TOKEN";
    private static final String unbindUrl = "https://api.weixin.qq.com/device/unbind?access_token=ACCESS_TOKEN";
    private static final String compelbindUrl = "https://api.weixin.qq.com/device/compel_bind?access_token=ACCESS_TOKEN";

    public static ApiResult transMsg(String deviceType, String deviceID, String openID, String content) {
        Kv data = Kv.by("device_type", deviceType);
        data.set("device_id", deviceID);
        data.set("open_id", openID);
        data.set("content", content);
        String url = TransMsgUrl.replace("ACCESS_TOKEN", AccessTokenApi.getAccessTokenStr());
        return new ApiResult(HttpUtils.post(url, JSON.toJSONString(data)));
    }

    public static ApiResult createQrcode(List<String> deviceIds) {
        Kv data = Kv.by("device_num", deviceIds.size());
        data.set("device_id_list", deviceIds);
        String url = CreateQrcode.replace("ACCESS_TOKEN", AccessTokenApi.getAccessTokenStr());
        return new ApiResult(HttpUtils.post(url, JSON.toJSONString(data)));
    }

    public static ApiResult createQrcodeNew(String productId) {
        String url = CreateQrcodeNew.replace("ACCESS_TOKEN", AccessTokenApi.getAccessTokenStr()).replace("PRODUCT_ID", productId);
        return new ApiResult(HttpUtils.get(url));
    }

    public static ApiResult bind(String tikect, String deviceid, String openid) {
        Kv data = Kv.by("ticket", tikect);
        data.set("device_id", deviceid);
        data.set("openid", openid);
        String url = bindUrl.replace("ACCESS_TOKEN", AccessTokenApi.getAccessTokenStr());
        return new ApiResult(HttpUtils.post(url, JSON.toJSONString(data)));
    }

    public static ApiResult compelBind(String deviceid, String openid) {
        Kv data = Kv.by("device_id", deviceid);
        data.set("openid", openid);
        String url = compelbindUrl.replace("ACCESS_TOKEN", AccessTokenApi.getAccessTokenStr());
        return new ApiResult(HttpUtils.post(url, JSON.toJSONString(data)));
    }

    public static ApiResult unbind(String tikect, String deviceid, String openid) {
        Kv data = Kv.by("ticket", tikect);
        data.set("device_id", deviceid);
        data.set("openid", openid);
        String url = unbindUrl.replace("ACCESS_TOKEN", AccessTokenApi.getAccessTokenStr());
        return new ApiResult(HttpUtils.post(url, JSON.toJSONString(data)));
    }

    public static ApiResult authorize(List<DeviceAuth> devices, boolean isCreate, String productId) {
        Kv data = Kv.by("device_num", String.valueOf(devices.size()));
        data.set("op_type", isCreate ? "0" : "1");
        data.set("product_id", productId);
        data.set("device_list", devices);
        String url = AuthorizeUrl.replace("ACCESS_TOKEN", AccessTokenApi.getAccessTokenStr());
        System.out.println(JSON.toJSONString(data));
        return new ApiResult(HttpUtils.post(url, JSON.toJSONString(data)));
    }

    public static ApiResult getStat(String deviceId) {
        String url = GetStatUrl.replace("DEVICE_ID", deviceId).replace("ACCESS_TOKEN", AccessTokenApi.getAccessTokenStr());
        return new ApiResult(HttpUtils.get(url));
    }

    public static ApiResult verifyQrcode(String ticket) {
        Kv data = Kv.by("ticket", ticket);
        String url = VerifyQrcodeUrl.replace("ACCESS_TOKEN", AccessTokenApi.getAccessTokenStr());
        return new ApiResult(HttpUtils.post(url, JSON.toJSONString(data)));
    }

    public static ApiResult getOpenId(String deviceType, String deviceId) {
        String url = GetOpenidUrl.replace("DEVICE_TYPE", deviceType).replace("DEVICE_ID", deviceId).replace("ACCESS_TOKEN", AccessTokenApi.getAccessTokenStr());
        return new ApiResult(HttpUtils.get(url));
    }
}
