/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.mobile.zwdd.config;

import com.sunwayworld.cloud.module.mobile.zwdd.config.ZwddProperties;
import com.sunwayworld.cloud.module.mobile.zwdd.utils.ZwddConfigStore;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;
import javax.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(value={ZwddProperties.class})
public class ZwddConfiguration {
    private ZwddProperties properties;
    private static Map<String, ZwddConfigStore> dtServices = new HashMap<String, ZwddConfigStore>();

    @Autowired
    public ZwddConfiguration(ZwddProperties properties) {
        this.properties = properties;
    }

    public static ZwddConfigStore getDtService(String agentId) {
        return dtServices.get(agentId);
    }

    @PostConstruct
    public void initServices() {
        if (this.properties != null && this.properties.getAppConfigs() != null && this.properties.getAppConfigs().size() > 0) {
            dtServices = this.properties.getAppConfigs().stream().map(a -> {
                ZwddProperties.ZwddAppConfig config = new ZwddProperties.ZwddAppConfig();
                config.setAgentId(a.getAgentId());
                config.setAppKey(a.getAppKey());
                config.setAppSecret(a.getAppSecret());
                config.setAppHomeUrl(a.getAppHomeUrl());
                config.setAppMsgUrl(a.getAppMsgUrl());
                ZwddConfigStore service = new ZwddConfigStore(config);
                return service;
            }).collect(Collectors.toMap(service -> service.getAppConfig().getAgentId(), a -> a));
        }
    }
}
