package com.sunwayworld.emc.projectfunction;

import java.util.function.Function;

import com.sunwayworld.GikamCoreApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;

import com.sunwayworld.framework.spring.SunwayApplication;
import com.sunwayworld.framework.spring.annotation.SunwayBootApplication;

@EnableAsync
@SunwayBootApplication
@EnableAutoConfiguration
public class GikamWpjcApplication extends GikamCoreApplication {
    public static void main(String[] args) {
        SunwayApplication.run(GikamWpjcApplication.class, args);
    }

    @Override
    public Function<HttpSecurity, HttpSecurity> securityConfigureCustomizer() {
        return r -> {
            try {
                r.authorizeRequests().antMatchers("/static/plugin/ueeditor/index.html", "/static/lims4/plugin/spread-sheets/src/index/index.html").denyAll();
                return r;
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        };
    }
}
