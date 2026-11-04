/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.mobile.zwdd.config;

import java.util.List;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix="zwdd.h5")
public class ZwddProperties {
    private List<ZwddAppConfig> appConfigs;

    public List<ZwddAppConfig> getAppConfigs() {
        return this.appConfigs;
    }

    public void setAppConfigs(List<ZwddAppConfig> appConfigs) {
        this.appConfigs = appConfigs;
    }

    public static class ZwddAppConfig {
        private String agentId;
        private String appKey;
        private String appSecret;
        private String appHomeUrl;
        private String appMsgUrl;

        public String getAgentId() {
            return this.agentId;
        }

        public void setAgentId(String agentId) {
            this.agentId = agentId;
        }

        public String getAppKey() {
            return this.appKey;
        }

        public void setAppKey(String appKey) {
            this.appKey = appKey;
        }

        public String getAppSecret() {
            return this.appSecret;
        }

        public void setAppSecret(String appSecret) {
            this.appSecret = appSecret;
        }

        public String getAppHomeUrl() {
            return this.appHomeUrl;
        }

        public void setAppHomeUrl(String appHomeUrl) {
            this.appHomeUrl = appHomeUrl;
        }

        public String getAppMsgUrl() {
            return this.appMsgUrl;
        }

        public void setAppMsgUrl(String appMsgUrl) {
            this.appMsgUrl = appMsgUrl;
        }
    }
}
