package com.sunwayworld.emc.framework.security;

import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;

import com.sunwayworld.module.sys.menu.service.CoreMenuService;

@Configuration
public class QueryAccessFilterConfiguration {

    @Bean
    public FilterRegistrationBean<QueryAccessFilter> queryAccessFilter(CoreMenuService menuService) {
        FilterRegistrationBean<QueryAccessFilter> registration = new FilterRegistrationBean<>();
        registration.setFilter(new QueryAccessFilter(menuService));
        registration.addUrlPatterns("/*");
        registration.setOrder(Ordered.LOWEST_PRECEDENCE);
        return registration;
    }
}
