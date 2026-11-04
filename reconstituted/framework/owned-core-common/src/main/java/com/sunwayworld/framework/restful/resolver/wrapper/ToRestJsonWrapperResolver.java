/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.framework.restful.resolver.wrapper;

import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.restful.resolver.wrapper.ToRestJsonWrapperResolveArgumentHandler;
import org.springframework.core.MethodParameter;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

public class ToRestJsonWrapperResolver
implements HandlerMethodArgumentResolver {
    @Override
    public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer container, NativeWebRequest webRequest, WebDataBinderFactory binderFactory) throws Exception {
        ToRestJsonWrapperResolveArgumentHandler resolveArgumentHandler = new ToRestJsonWrapperResolveArgumentHandler(parameter, webRequest);
        if (resolveArgumentHandler.supports()) {
            return resolveArgumentHandler.resolveArgument();
        }
        return RestJsonWrapperBean.newEmpty();
    }

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return parameter.getParameterType().equals(RestJsonWrapperBean.class);
    }
}
