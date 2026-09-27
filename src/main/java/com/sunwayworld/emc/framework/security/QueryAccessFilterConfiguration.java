package com.sunwayworld.emc.framework.security;

import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;

import com.sunwayworld.module.sys.menu.service.CoreMenuService;
import com.sunwayworld.module.item.file.service.CoreFileService;

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

    @Bean
    public FilterRegistrationBean<PersonnelSignatureFileFilter> personnelSignatureFileFilter(
            CoreFileService coreFileService, CoreMenuService menuService) {
        FilterRegistrationBean<PersonnelSignatureFileFilter> registration = new FilterRegistrationBean<>();
        registration.setFilter(new PersonnelSignatureFileFilter(coreFileService, menuService));
        registration.addUrlPatterns("/*");
        registration.setOrder(Ordered.LOWEST_PRECEDENCE - 1);
        return registration;
    }
}
