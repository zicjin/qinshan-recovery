/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.framework.fastdfs.properties;

import java.util.List;
import java.util.Map;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(value="sunway.fastdfs")
public class FastDfsProperties {
    private Map<String, List<String>> groups;

    public Map<String, List<String>> getGroups() {
        return this.groups;
    }

    public void setGroups(Map<String, List<String>> groups) {
        this.groups = groups;
    }
}
