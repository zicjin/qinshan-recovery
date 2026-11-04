/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.framework.jdk.tools;

import java.util.Map;

public class CompileMultipleResult {
    private Map<String, byte[]> bytesMap;
    private Map<String, String> errorMessageMap;

    public Map<String, byte[]> getBytesMap() {
        return this.bytesMap;
    }

    public void setBytesMap(Map<String, byte[]> bytesMap) {
        this.bytesMap = bytesMap;
    }

    public Map<String, String> getErrorMessageMap() {
        return this.errorMessageMap;
    }

    public void setErrorMessageMap(Map<String, String> errorMessageMap) {
        this.errorMessageMap = errorMessageMap;
    }
}
