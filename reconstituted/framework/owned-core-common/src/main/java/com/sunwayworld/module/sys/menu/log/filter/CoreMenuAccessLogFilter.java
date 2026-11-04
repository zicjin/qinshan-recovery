/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.module.sys.menu.log.filter;

import com.sunwayworld.framework.context.LocalContextHelper;
import com.sunwayworld.module.sys.menu.log.CoreMenuAccessLogQueue;
import com.sunwayworld.module.sys.menu.log.bean.CoreMenuAccessLogDTO;
import java.io.IOException;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

@Component
@ConditionalOnProperty(prefix="sunway.shortcut-menu.auto", name={"enabled"}, havingValue="true", matchIfMissing=false)
public class CoreMenuAccessLogFilter
implements Filter {
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest)request;
        LocalContextHelper.setServletRequest(req);
        if (LocalContextHelper.isUserLogin() && req.getHeader("sunway-menu") != null) {
            String menuId = req.getHeader("sunway-menu");
            CoreMenuAccessLogQueue.INSTANCE.offer(CoreMenuAccessLogDTO.of(LocalContextHelper.getLoginUserId(), menuId));
        }
        chain.doFilter(request, response);
    }
}
