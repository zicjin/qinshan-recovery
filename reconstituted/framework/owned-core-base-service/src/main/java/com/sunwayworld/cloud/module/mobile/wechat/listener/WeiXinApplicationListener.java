/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.mobile.wechat.listener;

import com.sunwayworld.cloud.module.mobile.wechat.weixin.api.ApiConfig;
import com.sunwayworld.cloud.module.mobile.wechat.weixin.api.ApiConfigKit;
import com.sunwayworld.framework.utils.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

@Component
public class WeiXinApplicationListener
implements ApplicationListener<ApplicationReadyEvent> {
    @Autowired
    private Environment environment;

    @Override
    public void onApplicationEvent(ApplicationReadyEvent event) {
        ApiConfig ac = new ApiConfig();
        ac.setToken(this.environment.getProperty("wechat.wx.token"));
        ac.setAppId(this.environment.getProperty("wechat.wx.appId"));
        ac.setAppSecret(this.environment.getProperty("wechat.wx.appSecret"));
        ac.setEncodingAesKey(this.environment.getProperty("wechat.wx.encodingAesKey"));
        ac.setEncryptMessage(this.environment.getProperty("wechat.wx.messageEncrypt", Boolean.class, false));
        if (!StringUtils.isEmpty(ac.getAppId())) {
            ApiConfigKit.putApiConfig(ac);
        }
    }
}
