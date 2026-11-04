/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.framework.context.filter;

import com.sunwayworld.framework.context.LocalContextHelper;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import java.io.IOException;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;

@GikamBean
@Component
public class MDCServletRequestFilter
implements Filter {
    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        try {
            MDC.put("USERID", LocalContextHelper.getLoginUserId());
            chain.doFilter(request, response);
        }
        finally {
            MDC.clear();
        }
    }
}
