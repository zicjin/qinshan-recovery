package com.sunwayworld.emc.projectfunction;

import java.util.function.Function;

import com.sunwayworld.GikamCoreApplication;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;

import com.sunwayworld.framework.spring.SunwayApplication;
import com.sunwayworld.framework.spring.annotation.SunwayBootApplication;

@EnableAsync
@SunwayBootApplication
public class GikamWpjcApplication extends GikamCoreApplication {
    public static void main(String[] args) {
        SunwayApplication.run(GikamWpjcApplication.class, args);
    }

    @Override
    public Function<HttpSecurity, HttpSecurity> securityConfigureCustomizer() {
        return r -> r;
    }
}
