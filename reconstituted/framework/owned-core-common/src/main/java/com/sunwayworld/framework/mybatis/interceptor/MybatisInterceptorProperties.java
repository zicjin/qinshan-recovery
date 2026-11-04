/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.framework.mybatis.interceptor;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix="sunway.mybatis")
public class MybatisInterceptorProperties {
    private String[] interceptors;

    public String[] getInterceptors() {
        return this.interceptors;
    }

    public void setInterceptors(String[] interceptors) {
        this.interceptors = interceptors;
    }
}
