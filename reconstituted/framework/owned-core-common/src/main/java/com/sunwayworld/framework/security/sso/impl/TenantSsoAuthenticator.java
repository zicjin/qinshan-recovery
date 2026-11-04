/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.framework.security.sso.impl;

import com.sunwayworld.framework.context.ApplicationContextHelper;
import com.sunwayworld.framework.security.bean.LoginUser;
import com.sunwayworld.framework.security.sso.SsoAuthenticator;
import com.sunwayworld.framework.utils.BeanUtils;
import com.sunwayworld.framework.utils.ServletUtils;
import com.sunwayworld.framework.utils.StringUtils;
import com.sunwayworld.module.mdm.user.bean.CoreUserBean;
import com.sunwayworld.module.mdm.user.service.CoreUserService;
import javax.servlet.http.HttpServletRequest;

public class TenantSsoAuthenticator
implements SsoAuthenticator {
    @Override
    public LoginUser authenticate() {
        HttpServletRequest request = ServletUtils.getCurrentRequest();
        String userId = request.getParameter("userId");
        if (!StringUtils.isEmpty(userId)) {
            String tenantId = request.getParameter("tenantId");
            String tenantName = request.getParameter("tenantName");
            if (StringUtils.isBlank(tenantId) || StringUtils.isBlank(tenantName)) {
                return null;
            }
            CoreUserService userService = ApplicationContextHelper.getBean(CoreUserService.class);
            CoreUserBean user = (CoreUserBean)userService.selectByIdIfPresent(userId);
            if (BeanUtils.isEmpty(user)) {
                throw new SecurityException("GIKAM.SECURITY.LOGIN.INCORRECT_USER_OR_PASSWORD");
            }
            LoginUser loginUser = new LoginUser(userId, user.getPassword());
            loginUser.setAdditionalCheck(false);
            loginUser.setTenantId(Long.parseLong(tenantId));
            loginUser.setTenantName(tenantName);
            return loginUser;
        }
        return null;
    }
}
