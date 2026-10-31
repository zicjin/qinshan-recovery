package com.sunwayworld.owned.internal.web;

import javax.servlet.DispatcherType;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.core.env.Environment;
import org.springframework.core.Ordered;

/** Loaded after identity registration; also denies protected calls without an identity backend. */
@AutoConfigureAfter(OwnedIdentityConfiguration.class)
@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.SERVLET)
public class OwnedQueryAccessConfiguration {
    @Bean public FilterRegistrationBean<OwnedQueryAccessFilter> ownedQueryAccessGuard(
            ObjectProvider<OwnedIdentityHttpBoundary> identity, Environment environment) {
        FilterRegistrationBean<OwnedQueryAccessFilter> registration = new FilterRegistrationBean<OwnedQueryAccessFilter>(
                new OwnedQueryAccessFilter(identity.getIfAvailable(), environment));
        registration.setName("ownedQueryAccessGuard");
        registration.addUrlPatterns("/*");
        registration.setOrder(Ordered.HIGHEST_PRECEDENCE + 50);
        registration.setDispatcherTypes(DispatcherType.REQUEST, DispatcherType.FORWARD,
                DispatcherType.INCLUDE, DispatcherType.ASYNC);
        return registration;
    }
}
