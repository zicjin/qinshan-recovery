/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.framework.fastdfs.loadbalancer;

import com.sunwayworld.framework.fastdfs.properties.FastDfsProperties;

public interface FastDfsRule {
    public FastDfsProperties getFastDfsProperties();

    public String getNextFastDfsUrl(String var1);
}
