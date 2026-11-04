/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.framework.spring;

import com.sunwayworld.framework.cache.memory.MemoryCacheManager;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.io.ResourceLoader;

public class SunwayApplication
extends SpringApplication {
    private SunwayApplication(Class<?> ... primarySources) {
        this((ResourceLoader)null, primarySources);
    }

    private SunwayApplication(ResourceLoader resourceLoader, Class<?> ... primarySources) {
        super(resourceLoader, primarySources);
        MemoryCacheManager.put("SunwayMainApplicationClass", this.getMainApplicationClass());
    }

    public static ConfigurableApplicationContext run(Class<?> primarySource, String ... args) {
        return SunwayApplication.run(new Class[]{primarySource}, args);
    }

    public static ConfigurableApplicationContext run(Class<?>[] primarySources, String[] args) {
        return new SunwayApplication(primarySources).run(args);
    }

    public static Class<?> getSunwayMainApplicationClass() {
        return (Class)MemoryCacheManager.get("SunwayMainApplicationClass");
    }
}
