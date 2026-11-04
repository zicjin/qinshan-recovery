/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.mobile.wechat.resource.impl;

import com.alibaba.fastjson.JSON;
import com.sunwayworld.cloud.module.mobile.wechat.helper.bean.ApiResult;
import com.sunwayworld.cloud.module.mobile.wechat.helper.bean.JsTicket;
import com.sunwayworld.cloud.module.mobile.wechat.helper.bean.weixin.SnsAccessToken;
import com.sunwayworld.cloud.module.mobile.wechat.helper.utils.HashKit;
import com.sunwayworld.cloud.module.mobile.wechat.resource.WeiXinResource;
import com.sunwayworld.cloud.module.mobile.wechat.weixin.api.ApiConfigKit;
import com.sunwayworld.cloud.module.mobile.wechat.weixin.api.JsTicketApi;
import com.sunwayworld.cloud.module.mobile.wechat.weixin.api.SnsAccessTokenApi;
import com.sunwayworld.cloud.module.mobile.wechat.weixin.api.UserApi;
import com.sunwayworld.framework.log.annotation.LogModule;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import com.sunwayworld.framework.utils.StringUtils;
import java.util.HashMap;
import java.util.UUID;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@LogModule(value="\u5fae\u4fe1\u516c\u4f17\u53f7\u63a5\u53e3")
@RestController
@GikamBean
@RequestMapping(value={"/open/core/module/sys/mobile/wechat/weixin"})
public class WeiXinResourceImpl
implements WeiXinResource {
    @Override
    public String getOpenId(String code) {
        SnsAccessToken snsAccessToken = SnsAccessTokenApi.getSnsAccessToken(ApiConfigKit.getAppId(), ApiConfigKit.getApiConfig().getAppSecret(), code);
        return JSON.toJSONString(snsAccessToken);
    }

    @Override
    public String getWxUser(String openid) {
        ApiResult apiResult = UserApi.getUserInfo(openid);
        return apiResult.getJson();
    }

    @Override
    public String getJsSign(String requestUrl) {
        JsTicket jsApiTicket = JsTicketApi.getTicket(JsTicketApi.JsApiType.jsapi);
        String jsapi_ticket = jsApiTicket.getTicket();
        String nonce_str = UUID.randomUUID().toString();
        String timestamp = Long.toString(System.currentTimeMillis() / 1000L);
        String url = "";
        if (!StringUtils.isEmpty(requestUrl) && (url = requestUrl).contains("#")) {
            url = url.substring(0, url.indexOf("#"));
        }
        String str = "jsapi_ticket=" + jsapi_ticket + "&noncestr=" + nonce_str + "&timestamp=" + timestamp + "&url=" + url;
        String signature = HashKit.sha1(str);
        HashMap<String, String> data = new HashMap<String, String>();
        data.put("signature", signature);
        data.put("appId", ApiConfigKit.getApiConfig().getAppId());
        data.put("nonceStr", nonce_str);
        data.put("timestamp", timestamp);
        data.put("jsapi_ticket", jsapi_ticket);
        return JSON.toJSONString(data);
    }
}
