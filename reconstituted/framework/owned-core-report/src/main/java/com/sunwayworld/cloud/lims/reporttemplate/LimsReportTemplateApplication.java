/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.lims.reporttemplate;

import com.sunwayworld.GikamCoreApplication;
import com.sunwayworld.framework.spring.SunwayApplication;
import com.sunwayworld.framework.spring.annotation.SunwayBootApplication;
import java.util.function.Function;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;

@SunwayBootApplication
@EnableAutoConfiguration
public class LimsReportTemplateApplication
extends GikamCoreApplication {
    public static void main(String[] args) {
        SunwayApplication.run(LimsReportTemplateApplication.class, args);
    }

    @Override
    public Function<HttpSecurity, HttpSecurity> securityConfigureCustomizer() {
        return r -> r;
    }
}
