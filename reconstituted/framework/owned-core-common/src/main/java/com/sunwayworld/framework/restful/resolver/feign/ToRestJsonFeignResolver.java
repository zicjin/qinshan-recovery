/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.framework.restful.resolver.feign;

import com.sunwayworld.framework.restful.resolver.AbstractJsonResolveArgumentHandler;
import com.sunwayworld.framework.restful.resolver.feign.ToRestJsonFeignResolverArgumentHandler;
import com.sunwayworld.framework.restful.resolver.feign.annotation.FeignRequestParam;
import org.springframework.core.MethodParameter;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

public class ToRestJsonFeignResolver
implements HandlerMethodArgumentResolver {
    @Override
    public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer container, NativeWebRequest webRequest, WebDataBinderFactory binderFactory) throws Exception {
        ToRestJsonFeignResolverArgumentHandler resolveArgumentHandler = new ToRestJsonFeignResolverArgumentHandler(parameter, webRequest);
        if (((AbstractJsonResolveArgumentHandler)resolveArgumentHandler).supports()) {
            return ((AbstractJsonResolveArgumentHandler)resolveArgumentHandler).resolveArgument();
        }
        return null;
    }

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return parameter.hasParameterAnnotation(FeignRequestParam.class);
    }
}
