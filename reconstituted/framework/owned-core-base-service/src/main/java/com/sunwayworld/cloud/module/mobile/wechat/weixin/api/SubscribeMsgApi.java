/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.mobile.wechat.weixin.api;

import com.alibaba.fastjson.JSON;
import com.sunwayworld.cloud.module.mobile.wechat.helper.bean.ApiResult;
import com.sunwayworld.cloud.module.mobile.wechat.helper.utils.Charsets;
import com.sunwayworld.cloud.module.mobile.wechat.helper.utils.HttpUtils;
import com.sunwayworld.cloud.module.mobile.wechat.weixin.api.AccessTokenApi;
import com.sunwayworld.cloud.module.mobile.wechat.weixin.api.ApiConfigKit;
import com.sunwayworld.cloud.module.mobile.wechat.weixin.api.Content;
import com.sunwayworld.cloud.module.mobile.wechat.weixin.api.Data;
import com.sunwayworld.cloud.module.mobile.wechat.weixin.api.SubscribeInfo;
import com.sunwayworld.framework.utils.StringUtils;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

public class SubscribeMsgApi {
    private static String subscribe = "https://api.weixin.qq.com/cgi-bin/message/template/subscribe?access_token=";
    private static String authorize_uri = "https://mp.weixin.qq.com/mp/subscribemsg?action=get_confirm";

    public static String getAuthorizeURL(String scene, String template_id, String redirectUri, String reserved) throws UnsupportedEncodingException {
        StringBuffer sbf = new StringBuffer();
        sbf.append(authorize_uri).append("&appid=").append(ApiConfigKit.getAppId()).append("&scene=").append(scene).append("&template_id=").append(template_id).append("&redirect_uri=").append(URLEncoder.encode(redirectUri, Charsets.UTF_8.name()).replace("+", "%20"));
        if (!StringUtils.isBlank(reserved)) {
            sbf.append("&reserved=").append(reserved);
        }
        sbf.append("#wechat_redirect");
        return sbf.toString();
    }

    public static ApiResult subscribe(String jsonStr) {
        String jsonResult = HttpUtils.post(subscribe + AccessTokenApi.getAccessTokenStr(), jsonStr);
        return new ApiResult(jsonResult);
    }

    public static ApiResult subscribe(SubscribeInfo subscribeInfo) {
        return new ApiResult(JSON.toJSONString(subscribeInfo));
    }

    public static ApiResult subscribe(String openId, String templateId, String url, int scene, String title, String value, String color) {
        SubscribeInfo subscribeInfo = SubscribeInfo.Builder().setTouser(openId).setTemplate_id(templateId).setUrl(url).setScene(String.valueOf(scene)).setTitle(title).setData(Data.Builder().setContent(Content.Builder().setValue(value).setColor(color).build()).build());
        return SubscribeMsgApi.subscribe(JSON.toJSONString(subscribeInfo));
    }
}
