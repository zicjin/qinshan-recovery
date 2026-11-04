/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.framework.restful.resolver.string;

import com.sunwayworld.framework.restful.resolver.AbstractJsonResolveArgumentHandler;
import com.sunwayworld.framework.utils.StringUtils;
import javax.servlet.http.HttpServletRequest;
import org.springframework.core.MethodParameter;
import org.springframework.web.context.request.NativeWebRequest;

public class ToJsonStringResolveArgumentHandler
extends AbstractJsonResolveArgumentHandler<String> {
    public ToJsonStringResolveArgumentHandler(MethodParameter parameter, NativeWebRequest webRequest) {
        super(parameter, webRequest);
    }

    @Override
    public boolean supports() {
        HttpServletRequest request = this.getWebRequest().getNativeRequest(HttpServletRequest.class);
        return !"GET".equals(request.getMethod()) && StringUtils.contains(request.getContentType(), "application/json");
    }

    @Override
    public String resolveArgument() {
        return this.getRequestJson();
    }
}
