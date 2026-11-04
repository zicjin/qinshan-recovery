/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.boot;

import java.util.List;
import java.util.Map;
import org.springframework.boot.autoconfigure.web.servlet.WebMvcProperties;
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

    public ApplicationMvcConfiguration(WebMvcProperties mvcProperties) {
        this.mvcProperties = mvcProperties;
    }

    @Override
    public native void configureDefaultServletHandling(DefaultServletHandlerConfigurer var1);

    @Override
    public native void addArgumentResolvers(List<HandlerMethodArgumentResolver> var1);

    @Override
    public native void configureMessageConverters(List<HttpMessageConverter<?>> var1);

    @Override
    protected native void addResourceHandlers(ResourceHandlerRegistry var1);

    @Override
    public native void configurePathMatch(PathMatchConfigurer var1);

    @Override
    public native void addInterceptors(InterceptorRegistry var1);

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
