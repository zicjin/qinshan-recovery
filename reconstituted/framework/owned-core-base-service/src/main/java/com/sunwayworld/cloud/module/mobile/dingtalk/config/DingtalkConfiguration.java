/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.mobile.dingtalk.config;

import com.sunwayworld.cloud.module.mobile.dingtalk.config.DingtalkProperties;
import com.sunwayworld.cloud.module.mobile.dingtalk.utils.dth5.DingtalkConfigStore;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;
import javax.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(value={DingtalkProperties.class})
public class DingtalkConfiguration {
    private DingtalkProperties properties;
    private static Map<String, DingtalkConfigStore> dtServiceMap = new HashMap<String, DingtalkConfigStore>();

    @Autowired
    public DingtalkConfiguration(DingtalkProperties properties) {
        this.properties = properties;
    }

    public static DingtalkConfigStore getDtService(String agentId) {
        return dtServiceMap.get(agentId);
    }

    @PostConstruct
    public void initServices() {
        if (this.properties != null && this.properties.getAppConfigs() != null && this.properties.getAppConfigs().size() > 0) {
            dtServiceMap = this.properties.getAppConfigs().stream().map(a -> {
                DingtalkProperties.DingtalkAppConfig config = new DingtalkProperties.DingtalkAppConfig();
                config.setAgentId(a.getAgentId());
                config.setAppKey(a.getAppKey());
                config.setAppSecret(a.getAppSecret());
                config.setAppHomeUrl(a.getAppHomeUrl());
                config.setAppMsgUrl(a.getAppMsgUrl());
                DingtalkConfigStore service = new DingtalkConfigStore(config);
                return service;
            }).collect(Collectors.toMap(service -> service.getAppConfig().getAgentId(), a -> a));
        }
    }
}
