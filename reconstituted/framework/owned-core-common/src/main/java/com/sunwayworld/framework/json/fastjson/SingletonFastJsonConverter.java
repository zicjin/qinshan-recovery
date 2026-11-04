/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.framework.json.fastjson;

import com.alibaba.fastjson.support.spring.FastJsonHttpMessageConverter;

public class SingletonFastJsonConverter
extends FastJsonHttpMessageConverter {
    private static volatile SingletonFastJsonConverter instance;

    private SingletonFastJsonConverter() {
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    public static SingletonFastJsonConverter getInstance() {
        if (instance != null) return instance;
        Class<SingletonFastJsonConverter> clazz = SingletonFastJsonConverter.class;
        synchronized (SingletonFastJsonConverter.class) {
            if (instance != null) return instance;
            instance = new SingletonFastJsonConverter();
            // ** MonitorExit[var0] (shouldn't be in output)
            return instance;
        }
    }
}
