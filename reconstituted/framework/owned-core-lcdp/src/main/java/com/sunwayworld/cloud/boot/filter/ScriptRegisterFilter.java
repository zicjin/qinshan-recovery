/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.boot.filter;

import com.sunwayworld.framework.cache.memory.MemoryCacheManager;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import com.sunwayworld.framework.utils.ServletUtils;
import java.io.IOException;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

@GikamBean
@Component
@ConditionalOnProperty(prefix="sunway.lcdp", name={"enable"}, havingValue="true", matchIfMissing=true)
public class ScriptRegisterFilter
implements Filter {
    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
    }

    @Override
    public void destroy() {
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        String flag = (String)MemoryCacheManager.get("REGISTER_FINISH_KEY");
        if (!"1".equals(flag)) {
            ServletUtils.responseError(HttpStatus.NOT_FOUND.value(), HttpStatus.NOT_FOUND.getReasonPhrase());
            return;
        }
        chain.doFilter(request, response);
    }
}
