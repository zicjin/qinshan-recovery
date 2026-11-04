/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud;

import com.sunwayworld.GikamCoreApplication;
import com.sunwayworld.framework.spring.SunwayApplication;
import com.sunwayworld.framework.spring.annotation.SunwayBootApplication;
import java.util.function.Function;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;

@SunwayBootApplication
@EnableDiscoveryClient
@EnableAutoConfiguration
public class GikamBaseApplication
extends GikamCoreApplication {
    public static void main(String[] args) {
        SunwayApplication.run(GikamBaseApplication.class, args);
    }

    @Override
    public Function<HttpSecurity, HttpSecurity> securityConfigureCustomizer() {
        return r -> r;
    }
}
