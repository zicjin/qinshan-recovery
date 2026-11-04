/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.framework.security.filter;

import com.sunwayworld.framework.context.ApplicationContextHelper;
import com.sunwayworld.framework.context.LocalContextHelper;
import com.sunwayworld.framework.security.AccountCategory;
import com.sunwayworld.framework.security.authentication.AuthenticationHelper;
import com.sunwayworld.framework.security.exception.AccountOfflineException;
import com.sunwayworld.framework.security.token.ResetTokenDTO;
import com.sunwayworld.framework.security.token.ResetTokenQueue;
import com.sunwayworld.framework.tenant.TenantContext;
import com.sunwayworld.framework.utils.ServletUtils;
import com.sunwayworld.framework.utils.StringUtils;
import java.io.IOException;
import java.util.Arrays;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.core.env.Environment;

public class RefreshFilterSecurityInterceptor
implements Filter {
    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
    }

    @Override
    public void destroy() {
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest httpServletRequest = (HttpServletRequest)request;
        String loginType = httpServletRequest.getParameter("loginType");
        if (LocalContextHelper.isUserLogin() && this.isResetLoginExpiredTimeRequest(httpServletRequest) && StringUtils.isEmpty(loginType)) {
            ResetTokenDTO resetToken = new ResetTokenDTO();
            resetToken.setMobile(ServletUtils.isMobileClient());
            resetToken.setToken(AuthenticationHelper.getLoginUserToken());
            resetToken.setTenant(TenantContext.getTenant());
            ResetTokenQueue.INSTANCE.offer(resetToken);
        }
        if (this.isHomepageRequest(httpServletRequest) && LocalContextHelper.isUserLogin()) {
            String activeMenuId = httpServletRequest.getParameter("activeMenuId");
            String redirectUrl = ServletUtils.getContextPath() + "/module/index/workspaces" + (!StringUtils.isEmpty(activeMenuId) ? "?activeMenuId=" + activeMenuId : "");
            ServletUtils.sendRedirect(httpServletRequest, (HttpServletResponse)response, redirectUrl);
            return;
        }
        if (!LocalContextHelper.isUserLogin()) {
            String rememberMeCookieToken = AuthenticationHelper.getLoginUserToken();
            if (StringUtils.isEmpty(rememberMeCookieToken)) {
                rememberMeCookieToken = httpServletRequest.getHeader("sunway-remember-me-cookie");
            }
            if (!StringUtils.isEmpty(rememberMeCookieToken)) {
                String category = AuthenticationHelper.getOfflineCategory(rememberMeCookieToken);
                if (AccountCategory.OFFLINE_PRELOGIN.name().equals(category)) {
                    throw new AccountOfflineException("GIKAM.SECURITY.LOGIN.OFFLINE_PRELOGIN");
                }
                if (AccountCategory.OFFLINE.name().equals(category)) {
                    throw new AccountOfflineException("GIKAM.SECURITY.LOGIN.OFFLINE");
                }
            }
        }
        chain.doFilter(request, response);
    }

    private boolean isHomepageRequest(HttpServletRequest request) {
        String requestUri = ServletUtils.getRequestUri(request);
        return "/".equals(requestUri);
    }

    private boolean isResetLoginExpiredTimeRequest(HttpServletRequest request) {
        Environment env = ApplicationContextHelper.getEnvironment();
        String unsetUris = env.getProperty("sunway.security.unset-login-expired-time-uris");
        if (StringUtils.isBlank(unsetUris)) {
            return true;
        }
        return Arrays.stream(StringUtils.split(unsetUris, ",")).noneMatch(u -> u.equals(ServletUtils.getRequestUri(request)));
    }
}
