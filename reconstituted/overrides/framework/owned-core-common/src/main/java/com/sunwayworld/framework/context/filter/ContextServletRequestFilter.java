/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.framework.context.filter;

import com.sunwayworld.framework.context.LocalContextHelper;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Component;

@Component
public class ContextServletRequestFilter
implements Filter {
    private static final Logger logger = LogManager.getLogger(ContextServletRequestFilter.class);

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        LocalContextHelper.setServletRequest(request);
        LocalContextHelper.setServletResponse(response);
        try {
            chain.doFilter(request, response);
        } finally {
            LocalContextHelper.removeServletRequest();
            LocalContextHelper.removeServletResponse();
        }
    }

    @Override
    public void destroy() {
    }
}
