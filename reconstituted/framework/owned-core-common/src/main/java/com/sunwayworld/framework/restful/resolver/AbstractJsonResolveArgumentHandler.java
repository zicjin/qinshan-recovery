/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.framework.restful.resolver;

import com.sunwayworld.framework.exception.UnexpectedException;
import com.sunwayworld.framework.utils.StringUtils;
import java.io.IOException;
import java.util.Objects;
import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import org.springframework.core.MethodParameter;
import org.springframework.web.context.request.NativeWebRequest;

public abstract class AbstractJsonResolveArgumentHandler<T> {
    private final MethodParameter parameter;
    private final NativeWebRequest webRequest;

    public AbstractJsonResolveArgumentHandler(MethodParameter parameter, NativeWebRequest webRequest) {
        this.parameter = Objects.requireNonNull(parameter);
        this.webRequest = Objects.requireNonNull(webRequest);
    }

    public MethodParameter getParameter() {
        return this.parameter;
    }

    public NativeWebRequest getWebRequest() {
        return this.webRequest;
    }

    /*
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     */
    protected String getRequestJson() {
        if (!this.supports()) {
            return null;
        }
        HttpServletRequest request = this.getWebRequest().getNativeRequest(HttpServletRequest.class);
        try (ServletInputStream is = request.getInputStream();){
            String string = StringUtils.read(is);
            return string;
        }
        catch (IOException ioe) {
            throw new UnexpectedException(ioe);
        }
    }

    public abstract boolean supports();

    public abstract T resolveArgument();
}
