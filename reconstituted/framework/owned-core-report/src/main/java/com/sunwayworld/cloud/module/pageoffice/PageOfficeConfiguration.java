/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.pageoffice;

import com.zhuozhengsoft.pageoffice.poserver.Server;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
@Profile(value={"pageoffice"})
public class PageOfficeConfiguration {
    @Value(value="${sunway.config.pageoffice.license.path}")
    private String pageOfficeLicensePath;

    @Bean
    public ServletRegistrationBean servletRegistrationBean() {
        Server poserver = new Server();
        poserver.setSysPath(this.pageOfficeLicensePath);
        ServletRegistrationBean<Server> servletRegistration = new ServletRegistrationBean<Server>(poserver, new String[0]);
        servletRegistration.addUrlMappings("/poserver.zz");
        servletRegistration.addUrlMappings("/posetup.exe");
        servletRegistration.addUrlMappings("/pageoffice.js");
        servletRegistration.addUrlMappings("/jquery.min.js");
        servletRegistration.addUrlMappings("/pobstyle.css");
        servletRegistration.addUrlMappings("/sealsetup.exe");
        servletRegistration.addUrlMappings("/poclient");
        return servletRegistration;
    }
}
