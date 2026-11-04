/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.framework.jdk.tools;

import java.util.Map;

public class LoadMultipleResult {
    private Map<String, Class<?>> classMap;
    private Map<String, String> errorMessageMap;

    public Map<String, Class<?>> getClassMap() {
        return this.classMap;
    }

    public void setClassMap(Map<String, Class<?>> classMap) {
        this.classMap = classMap;
    }

    public Map<String, String> getErrorMessageMap() {
        return this.errorMessageMap;
    }

    public void setErrorMessageMap(Map<String, String> errorMessageMap) {
        this.errorMessageMap = errorMessageMap;
    }
}
