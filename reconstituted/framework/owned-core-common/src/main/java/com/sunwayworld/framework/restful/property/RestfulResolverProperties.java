/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.framework.restful.property;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix="sunway.config.system.common")
public class RestfulResolverProperties {
    private boolean restJsonWrapperEnable = true;

    public boolean isRestJsonWrapperEnable() {
        return this.restJsonWrapperEnable;
    }

    public void setRestJsonWrapperEnable(boolean restJsonWrapperEnable) {
        this.restJsonWrapperEnable = restJsonWrapperEnable;
    }
}
