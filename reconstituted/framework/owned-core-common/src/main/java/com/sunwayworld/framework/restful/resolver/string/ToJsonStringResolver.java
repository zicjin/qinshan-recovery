/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.framework.restful.resolver.string;

import com.sunwayworld.framework.restful.resolver.string.ToJsonStringResolveArgumentHandler;
import com.sunwayworld.framework.restful.resolver.string.annotation.Json;
import org.springframework.core.MethodParameter;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

public class ToJsonStringResolver
implements HandlerMethodArgumentResolver {
    @Override
    public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer container, NativeWebRequest webRequest, WebDataBinderFactory binderFactory) throws Exception {
        ToJsonStringResolveArgumentHandler resolveArgumentHandler = new ToJsonStringResolveArgumentHandler(parameter, webRequest);
        if (resolveArgumentHandler.supports()) {
            return resolveArgumentHandler.resolveArgument();
        }
        return "";
    }

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return parameter.getParameterType().equals(String.class) && parameter.hasParameterAnnotation(Json.class);
    }
}
