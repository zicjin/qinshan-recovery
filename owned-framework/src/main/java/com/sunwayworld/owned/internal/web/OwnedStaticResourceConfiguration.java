package com.sunwayworld.owned.internal.web;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/** Preserve the original application's /static/... resource URLs. */
@Configuration
public class OwnedStaticResourceConfiguration implements WebMvcConfigurer {
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/static/**").addResourceLocations("classpath:/static/");
        // Older project templates and JavaScript still emit /emc/... URLs.
        // Keep that public path as an alias of the source-owned static/emc
        // tree instead of rewriting every recovered business asset.
        registry.addResourceHandler("/emc/**").addResourceLocations("classpath:/static/emc/");
    }
}
