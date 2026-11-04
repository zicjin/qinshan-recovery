/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.framework.progress.core;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix="sunway.progress")
public class ProgressProperties {
    private Mode mode;
    private Cache cache;

    public Mode getMode() {
        return this.mode;
    }

    public void setMode(Mode mode) {
        this.mode = mode;
    }

    public Cache getCache() {
        return this.cache;
    }

    public void setCache(Cache cache) {
        this.cache = cache;
    }

    public static class Mode {
        private Boolean async;

        public Boolean getAsync() {
            return this.async;
        }

        public void setAsync(Boolean async) {
            this.async = async;
        }
    }

    public static class Cache {
        private String name;
        private Integer timeout;

        public String getName() {
            return this.name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public Integer getTimeout() {
            return this.timeout;
        }

        public void setTimeout(Integer timeout) {
            this.timeout = timeout;
        }
    }
}
