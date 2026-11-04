/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.framework.jdk.hints.core;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix="sunway.code-hints")
public class CodeHintsProperties {
    private String[] packageNames;
    private String[] classNames;

    public String[] getPackageNames() {
        return this.packageNames;
    }

    public void setPackageNames(String[] packageNames) {
        this.packageNames = packageNames;
    }

    public String[] getClassNames() {
        return this.classNames;
    }

    public void setClassNames(String[] classNames) {
        this.classNames = classNames;
    }
}
