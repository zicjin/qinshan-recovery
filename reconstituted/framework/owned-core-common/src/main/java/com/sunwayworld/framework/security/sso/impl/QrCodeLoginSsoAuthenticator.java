/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.framework.security.sso.impl;

import com.sunwayworld.framework.context.ApplicationContextHelper;
import com.sunwayworld.framework.security.bean.LoginUser;
import com.sunwayworld.framework.security.bean.QrCodeLoginStatusDTO;
import com.sunwayworld.framework.security.provider.service.QrCodeLoginService;
import com.sunwayworld.framework.security.sso.SsoAuthenticator;
import com.sunwayworld.framework.utils.ServletUtils;
import com.sunwayworld.module.mdm.user.bean.CoreUserBean;
import com.sunwayworld.module.mdm.user.service.CoreUserService;
import javax.servlet.http.HttpServletRequest;

public class QrCodeLoginSsoAuthenticator
implements SsoAuthenticator {
    @Override
    public LoginUser authenticate() {
        HttpServletRequest request = ServletUtils.getCurrentRequest();
        String qrToken = request.getParameter("qrToken");
        QrCodeLoginService qrCodeLoginService = ApplicationContextHelper.getBean(QrCodeLoginService.class);
        QrCodeLoginStatusDTO loginStatus = qrCodeLoginService.getLoginStatus(qrToken);
        if ("2".equals(loginStatus.getStatus())) {
            CoreUserService userService = ApplicationContextHelper.getBean(CoreUserService.class);
            CoreUserBean user = (CoreUserBean)userService.selectById(loginStatus.getUserId());
            return new LoginUser(user.getId(), user.getPassword());
        }
        return null;
    }
}
