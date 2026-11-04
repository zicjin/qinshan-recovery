/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.monitor.core.filter;

import com.sunwayworld.cloud.module.monitor.core.cache.CoreMonitorReqCache;
import com.sunwayworld.framework.context.LocalContextHelper;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import java.io.IOException;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@GikamBean
@Component
@Profile(value={"monitor"})
public class CoreMonitorFilter
implements Filter {
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        CoreMonitorReqCache.INSTANCE.increaseQps();
        if (LocalContextHelper.isUserLogin()) {
            CoreMonitorReqCache.INSTANCE.increaseCus(LocalContextHelper.getLoginUserId());
        }
        chain.doFilter(request, response);
    }
}
