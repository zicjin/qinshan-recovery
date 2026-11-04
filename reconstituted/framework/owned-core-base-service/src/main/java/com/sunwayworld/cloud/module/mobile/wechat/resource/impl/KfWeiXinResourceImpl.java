/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.mobile.wechat.resource.impl;

import com.alibaba.fastjson.JSON;
import com.sunwayworld.cloud.module.mobile.wechat.helper.bean.ApiResult;
import com.sunwayworld.cloud.module.mobile.wechat.helper.bean.kfweixin.SnsAccessToken;
import com.sunwayworld.cloud.module.mobile.wechat.kfweixin.api.ApiConfigKit;
import com.sunwayworld.cloud.module.mobile.wechat.kfweixin.api.SnsAccessTokenApi;
import com.sunwayworld.cloud.module.mobile.wechat.kfweixin.api.UserApi;
import com.sunwayworld.cloud.module.mobile.wechat.resource.KfWeiXinResource;
import com.sunwayworld.framework.log.annotation.LogModule;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@LogModule(value="\u5fae\u4fe1\u5f00\u653e\u5e73\u53f0\u63a5\u53e3")
@RestController
@GikamBean
@RequestMapping(value={"/open/core/module/sys/mobile/wechat/kfweixin"})
public class KfWeiXinResourceImpl
implements KfWeiXinResource {
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
}
