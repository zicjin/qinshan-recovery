/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.boot;

import java.util.List;
import java.util.Map;
import org.springframework.boot.autoconfigure.web.servlet.WebMvcProperties;
import com.sunwayworld.framework.restful.resolver.feign.ToRestJsonFeignResolver;
import com.sunwayworld.framework.restful.resolver.string.ToJsonStringResolver;
import com.sunwayworld.framework.restful.resolver.wrapper.ToRestJsonWrapperResolver;
import com.sunwayworld.framework.tenant.TenantInterceptor;
import com.sunwayworld.framework.json.fastjson.SingletonFastJsonConverter;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.FilterType;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.ContentNegotiationConfigurer;
import org.springframework.web.servlet.config.annotation.DefaultServletHandlerConfigurer;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.PathMatchConfigurer;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport;

@Configuration
@ComponentScan(useDefaultFilters=false, basePackages={"com.sunwayworld"}, includeFilters={@ComponentScan.Filter(type=FilterType.ANNOTATION, value={Controller.class}), @ComponentScan.Filter(type=FilterType.ANNOTATION, value={RestController.class})}, excludeFilters={@ComponentScan.Filter(type=FilterType.ASPECTJ, pattern={"com.sunwayworld.test..*"})})
public class ApplicationMvcConfiguration
extends WebMvcConfigurationSupport {
    private final WebMvcProperties mvcProperties;

    private final org.springframework.web.servlet.i18n.LocaleChangeInterceptor localeChangeInterceptor = new org.springframework.web.servlet.i18n.LocaleChangeInterceptor();

    public ApplicationMvcConfiguration(WebMvcProperties mvcProperties) {
        this.mvcProperties = mvcProperties;
    }

    @Override
    public void configureDefaultServletHandling(DefaultServletHandlerConfigurer configurer) { configurer.enable(); }

    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
        resolvers.add(new ToRestJsonWrapperResolver());
        resolvers.add(new ToJsonStringResolver());
        resolvers.add(new ToRestJsonFeignResolver());
    }

    @Override
    public void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
        converters.add(SingletonFastJsonConverter.getInstance());
    }

    @Override
    protected void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/static/**").addResourceLocations("classpath:/static/");
    }

    @Override
    public void configurePathMatch(PathMatchConfigurer configurer) {
        configurer.setUseSuffixPatternMatch(false);
        configurer.setUseTrailingSlashMatch(true);
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new TenantInterceptor());
        registry.addInterceptor(this.localeChangeInterceptor);
    }

    @Override
    protected void configureContentNegotiation(ContentNegotiationConfigurer configurer) {
        WebMvcProperties.Contentnegotiation contentnegotiation = this.mvcProperties.getContentnegotiation();
        configurer.favorPathExtension(contentnegotiation.isFavorPathExtension());
        configurer.favorParameter(contentnegotiation.isFavorParameter());
        if (contentnegotiation.getParameterName() != null) {
            configurer.parameterName(contentnegotiation.getParameterName());
        }
        Map<String, MediaType> mediaTypes = this.mvcProperties.getContentnegotiation().getMediaTypes();
        mediaTypes.forEach(configurer::mediaType);
    }
}
