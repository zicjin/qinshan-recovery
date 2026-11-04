/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.framework.netty.config;

import com.sunwayworld.framework.netty.core.NettyStarterRegistry;
import javax.annotation.PreDestroy;
import org.springframework.context.annotation.Configuration;

@Configuration
public class NettyAutoConfiguration {
    @PreDestroy
    public void destroy() {
        NettyStarterRegistry.INSTANCE.getStarterSet().parallelStream().forEach(s -> s.destroy());
    }
}
