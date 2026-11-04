/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.mobile.wechat.listener;

import com.sunwayworld.cloud.module.mobile.wechat.qyweixin.api.ApiConfig;
import com.sunwayworld.cloud.module.mobile.wechat.qyweixin.api.ApiConfigKit;
import com.sunwayworld.framework.utils.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

@Component
public class QyWeiXinApplicationListener
implements ApplicationListener<ApplicationReadyEvent> {
    @Autowired
    private Environment environment;

    @Override
    public void onApplicationEvent(ApplicationReadyEvent event) {
        ApiConfig ac = new ApiConfig();
        ac.setToken(this.environment.getProperty("wechat.qywx.token"));
        ac.setCorpId(this.environment.getProperty("wechat.qywx.corpId"));
        ac.setCorpSecret(this.environment.getProperty("wechat.qywx.corpSecret"));
        ac.setAgentId(this.environment.getProperty("wechat.qywx.agentId"));
        ac.setEncodingAesKey(this.environment.getProperty("wechat.qywx.encodingAesKey"));
        ac.setEncryptMessage(this.environment.getProperty("wechat.qywx.messageEncrypt", Boolean.class, false));
        if (!StringUtils.isEmpty(ac.getCorpId())) {
            ApiConfigKit.putApiConfig(ac);
        }
    }
}
