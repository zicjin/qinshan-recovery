/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.framework.thymeleaf.templateresolver;

import com.sunwayworld.framework.context.ApplicationContextHelper;
import com.sunwayworld.framework.thymeleaf.templateresource.GikamDesignStudioResourceTemplateResource;
import com.sunwayworld.framework.thymeleaf.templateresource.GikamPageConfigResourceTemplateResource;
import java.util.Map;
import org.springframework.context.ApplicationContext;
import org.springframework.core.io.Resource;
import org.thymeleaf.IEngineConfiguration;
import org.thymeleaf.templateresolver.AbstractConfigurableTemplateResolver;
import org.thymeleaf.templateresource.ITemplateResource;

public class GikamResourceTemplateResolver
extends AbstractConfigurableTemplateResolver {
    private ApplicationContext applicationContext;

    public GikamResourceTemplateResolver(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    @Override
    protected ITemplateResource computeTemplateResource(IEngineConfiguration configuration, String ownerTemplate, String template, String resourceName, String characterEncoding, Map<String, Object> templateResolutionAttributes) {
        Resource resource = this.applicationContext.getResource(this.getPrefix() + template + this.getSuffix());
        if (ApplicationContextHelper.getEnvironment().getProperty("sunway.design.studio.enabled", Boolean.class, Boolean.FALSE).booleanValue()) {
            return new GikamDesignStudioResourceTemplateResource(resource, characterEncoding);
        }
        return new GikamPageConfigResourceTemplateResource(resource, characterEncoding);
    }
}
