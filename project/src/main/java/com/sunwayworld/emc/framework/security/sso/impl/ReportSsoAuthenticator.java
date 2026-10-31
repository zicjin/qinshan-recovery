// 2026-09-24 从生产字节码反编译恢复（CFR 0.152）：生产新增类，历史源码缺失
package com.sunwayworld.emc.framework.security.sso.impl;

import com.sunwayworld.framework.context.ApplicationContextHelper;
import com.sunwayworld.framework.security.bean.LoginUser;
import com.sunwayworld.framework.security.sso.SsoAuthenticator;
import com.sunwayworld.framework.utils.ServletUtils;
import com.sunwayworld.module.mdm.user.bean.CoreUserBean;
import com.sunwayworld.module.mdm.user.service.CoreUserService;
import javax.servlet.http.HttpServletRequest;
import org.springframework.security.authentication.BadCredentialsException;

public class ReportSsoAuthenticator
implements SsoAuthenticator {
    @Override
    public LoginUser authenticate() {
        HttpServletRequest request = ServletUtils.getCurrentRequest();
        String userId = request.getParameter("userId");
        CoreUserService userService = ApplicationContextHelper.getBean(CoreUserService.class);
        CoreUserBean user = (CoreUserBean)userService.selectByIdIfPresent(userId);
        if (user == null) {
            throw new BadCredentialsException("GIKAM.SECURITY.LOGIN.INCORRECT_USER_OR_PASSWORD");
        }
        LoginUser loginUser = new LoginUser(userId, user.getPassword());
        loginUser.setAdditionalCheck(false);
        return loginUser;
    }

    @Override
    public boolean login() {
        return false;
    }

    @Override
    public void logout() {
    }
}
