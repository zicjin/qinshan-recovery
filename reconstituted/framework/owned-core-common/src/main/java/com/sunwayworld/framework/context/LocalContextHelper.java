/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.framework.context;

import com.sunwayworld.framework.i18n.I18nHelper;
import com.sunwayworld.framework.security.authentication.AuthenticationHelper;
import com.sunwayworld.framework.security.bean.LoginUser;
import com.sunwayworld.framework.utils.StringUtils;
import com.sunwayworld.module.mdm.user.bean.CoreUserBean;
import com.sunwayworld.module.mdm.user.service.CoreUserService;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
public class LocalContextHelper {
    private static final ThreadLocal<ServletRequest> SERVLET_REQUEST_CONTAIER = new ThreadLocal();
    private static final ThreadLocal<ServletResponse> SERVLET_RESPONSE_CONTAIER = new ThreadLocal();
    private static CoreUserService userService;

    @Autowired(required=true)
    @Lazy
    public void setCoreUserService(CoreUserService userService) {
        LocalContextHelper.userService = userService;
    }

    public static void setUserLogin(final LoginUser loginUser) {
        if (loginUser != null && !loginUser.getUsername().equals(LocalContextHelper.getLoginUserId())) {
            SecurityContextHolder.getContext().setAuthentication(new AbstractAuthenticationToken(null){
                private static final long serialVersionUID = -6852867025889280418L;

                @Override
                public Object getCredentials() {
                    return null;
                }

                @Override
                public Object getPrincipal() {
                    return loginUser;
                }

                @Override
                public boolean isAuthenticated() {
                    return true;
                }
            });
        }
    }

    public static void removeUserLogin() {
        SecurityContextHolder.getContext().setAuthentication(null);
    }

    public static boolean isUserLogin() {
        return LocalContextHelper.getLoginAuthentication() != null;
    }

    public static boolean isAdminLogin() {
        if (LocalContextHelper.isUserLogin()) {
            return "sysAdmin".equals(LocalContextHelper.getLoginUser().getUserType());
        }
        return false;
    }

    public static boolean isTrusteeLogin() {
        LoginUser loginUser = LocalContextHelper.getLoginPrincipal();
        if (loginUser == null) {
            return false;
        }
        return loginUser.getTrustee() != null;
    }

    public static String getLoginUserId() {
        return LocalContextHelper.getLoginUser().getId();
    }

    public static String getLoginUserName() {
        LoginUser loginUser = LocalContextHelper.getLoginPrincipal();
        if (loginUser == null) {
            return null;
        }
        CoreUserBean user = (CoreUserBean)userService.selectById(loginUser.getUsername());
        if (loginUser.getTrustee() == null) {
            return user.getUserName();
        }
        CoreUserBean trustee = (CoreUserBean)userService.selectById(loginUser.getTrustee().getUsername());
        return user.getUserName() + "(" + I18nHelper.getMessage("GIKAM.ABBR.TRUSTEENAME", new String[0]) + ":" + trustee.getUserName() + ")";
    }

    public static String getLoginOrgId() {
        Authentication authentication = LocalContextHelper.getLoginAuthentication();
        if (authentication == null) {
            return null;
        }
        return ((LoginUser)authentication.getPrincipal()).getOrgId();
    }

    public static String getLoginOrgName() {
        Authentication authentication = LocalContextHelper.getLoginAuthentication();
        if (authentication == null) {
            return null;
        }
        return ((LoginUser)authentication.getPrincipal()).getOrgName();
    }

    public static Long getLoginRoleId() {
        Authentication authentication = LocalContextHelper.getLoginAuthentication();
        if (authentication == null) {
            return null;
        }
        return ((LoginUser)authentication.getPrincipal()).getRoleId();
    }

    public static String getLoginRoleName() {
        Authentication authentication = LocalContextHelper.getLoginAuthentication();
        if (authentication == null) {
            return null;
        }
        return ((LoginUser)authentication.getPrincipal()).getRoleName();
    }

    public static CoreUserBean getLoginUser() {
        Authentication authentication = LocalContextHelper.getLoginAuthentication();
        if (authentication == null) {
            return new CoreUserBean();
        }
        return (CoreUserBean)userService.selectById(authentication.getName());
    }

    public static LoginUser getLoginPrincipal() {
        Authentication authentication = LocalContextHelper.getLoginAuthentication();
        if (authentication == null) {
            return null;
        }
        return (LoginUser)authentication.getPrincipal();
    }

    public static Long getLoginTenantId() {
        Authentication authentication = LocalContextHelper.getLoginAuthentication();
        if (authentication == null) {
            return null;
        }
        return ((LoginUser)authentication.getPrincipal()).getTenantId();
    }

    public static String getLoginTenantName() {
        Authentication authentication = LocalContextHelper.getLoginAuthentication();
        if (authentication == null) {
            return null;
        }
        return ((LoginUser)authentication.getPrincipal()).getTenantName();
    }

    public static void setServletRequest(ServletRequest request) {
        SERVLET_REQUEST_CONTAIER.set(request);
    }

    public static HttpServletRequest getHttpServletRequest() {
        return (HttpServletRequest)SERVLET_REQUEST_CONTAIER.get();
    }

    public static void removeServletRequest() {
        SERVLET_REQUEST_CONTAIER.remove();
    }

    public static void setServletResponse(ServletResponse response) {
        SERVLET_RESPONSE_CONTAIER.set(response);
    }

    public static HttpServletResponse getHttpServletResponse() {
        return (HttpServletResponse)SERVLET_RESPONSE_CONTAIER.get();
    }

    public static void removeServletResponse() {
        SERVLET_RESPONSE_CONTAIER.remove();
    }

    private static Authentication getLoginAuthentication() {
        String userToken;
        SecurityContext sc = SecurityContextHolder.getContext();
        Authentication authentication = sc.getAuthentication();
        if (authentication == null && !StringUtils.isEmpty(userToken = AuthenticationHelper.getLoginUserToken())) {
            authentication = AuthenticationHelper.getCacheAuthentication(userToken);
        }
        if (authentication != null && (StringUtils.isEmpty(authentication.getName()) || "anonymousUser".equals(authentication.getName()))) {
            return null;
        }
        if (authentication != null) {
            sc.setAuthentication(authentication);
        }
        return authentication;
    }
}
