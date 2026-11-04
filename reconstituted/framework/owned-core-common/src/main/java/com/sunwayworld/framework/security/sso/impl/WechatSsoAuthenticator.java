/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.framework.security.sso.impl;

import com.sunwayworld.framework.context.ApplicationContextHelper;
import com.sunwayworld.framework.database.sql.Order;
import com.sunwayworld.framework.security.bean.LoginUser;
import com.sunwayworld.framework.security.service.DefaultUserDetailsChecker;
import com.sunwayworld.framework.security.sso.SsoAuthenticator;
import com.sunwayworld.framework.utils.ServletUtils;
import com.sunwayworld.framework.utils.StringUtils;
import com.sunwayworld.module.mdm.user.bean.CoreUserBean;
import com.sunwayworld.module.mdm.user.service.CoreUserService;
import com.sunwayworld.module.sys.wechat.bean.CoreWechatUserBean;
import com.sunwayworld.module.sys.wechat.service.CoreWechatUserService;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import org.springframework.security.crypto.password.PasswordEncoder;

public class WechatSsoAuthenticator
implements SsoAuthenticator {
    @Override
    public LoginUser authenticate() {
        HttpServletRequest request = ServletUtils.getCurrentRequest();
        String openId = request.getParameter("openid");
        if (!StringUtils.isEmpty(openId)) {
            CoreUserService userService = ApplicationContextHelper.getBean(CoreUserService.class);
            CoreWechatUserService wechatUserService = ApplicationContextHelper.getBean(CoreWechatUserService.class);
            String id = request.getParameter("id");
            if (!StringUtils.isEmpty(id)) {
                PasswordEncoder passwordEncoder = ApplicationContextHelper.getBean(PasswordEncoder.class);
                DefaultUserDetailsChecker.checkPassword(id, passwordEncoder.encode(request.getParameter("password")));
                boolean exist = false;
                CoreWechatUserBean filter = new CoreWechatUserBean();
                filter.setId(id);
                filter.setOpenId(openId);
                List<CoreWechatUserBean> wechatUserList = wechatUserService.getDao().selectUnionList((CoreWechatUserBean)filter, new Order[0]);
                for (CoreWechatUserBean wechatUser : wechatUserList) {
                    if (!openId.equals(wechatUser.getOpenId()) || !id.equals(wechatUser.getId())) {
                        wechatUserService.delete(wechatUser.getId());
                        continue;
                    }
                    exist = true;
                }
                if (!exist) {
                    wechatUserService.insert(id, openId);
                }
                LoginUser loginUser = new LoginUser(id, request.getParameter("password"));
                loginUser.setAdditionalCheck(true);
                return loginUser;
            }
            CoreWechatUserBean filter = new CoreWechatUserBean();
            filter.setOpenId(openId);
            List<CoreWechatUserBean> wechatUserList = wechatUserService.selectList(filter, new Order[0]);
            if (wechatUserList.size() == 1) {
                CoreUserBean user = (CoreUserBean)userService.selectByIdIfPresent(wechatUserList.get(0).getId());
                LoginUser loginUser = new LoginUser(user.getId(), user.getPassword());
                loginUser.setAdditionalCheck(false);
                return loginUser;
            }
        }
        throw new SecurityException("GIKAM.SECURITY.SSO_LOGIN.INVALID_DATA");
    }

    @Override
    public boolean login() {
        return false;
    }

    @Override
    public void logout() {
        HttpServletRequest request = ServletUtils.getCurrentRequest();
        String openId = request.getParameter("openid");
        if (!StringUtils.isEmpty(openId)) {
            CoreWechatUserService wechatUserService = ApplicationContextHelper.getBean(CoreWechatUserService.class);
            CoreWechatUserBean filter = new CoreWechatUserBean();
            filter.setOpenId(openId);
            wechatUserService.getDao().deleteBy((CoreWechatUserBean)filter, "OPENID");
        }
    }
}
