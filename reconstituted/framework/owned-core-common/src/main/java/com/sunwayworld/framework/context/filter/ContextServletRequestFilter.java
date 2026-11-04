/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.framework.context.filter;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ContextServletRequestFilter
implements Filter {
    private static final Logger logger = LogManager.getLogger(ContextServletRequestFilter.class);

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
    }

    @Override
    public native void doFilter(ServletRequest var1, ServletResponse var2, FilterChain var3);

    @Override
    public void destroy() {
    }
}
