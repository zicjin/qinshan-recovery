/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.mobile.dingtalk.sso;

import com.sunwayworld.cloud.module.mobile.dingtalk.bean.DingtalkUserBean;
import com.sunwayworld.cloud.module.mobile.dingtalk.service.DingtalkUserService;
import com.sunwayworld.framework.context.ApplicationContextHelper;
import com.sunwayworld.framework.database.sql.Order;
import com.sunwayworld.framework.security.bean.LoginUser;
import com.sunwayworld.framework.security.service.DefaultUserDetailsChecker;
import com.sunwayworld.framework.security.sso.SsoAuthenticator;
import com.sunwayworld.framework.utils.ServletUtils;
import com.sunwayworld.framework.utils.StringUtils;
import com.sunwayworld.module.mdm.user.bean.CoreUserBean;
import com.sunwayworld.module.mdm.user.service.CoreUserService;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import org.springframework.security.crypto.password.PasswordEncoder;

public class DingtalkSsoAuthenticator
implements SsoAuthenticator {
    @Override
    public LoginUser authenticate() {
        HttpServletRequest request = ServletUtils.getCurrentRequest();
        String openId = request.getParameter("openid");
        String agentId = request.getParameter("agentid");
        if (!StringUtils.isEmpty(openId) && !StringUtils.isEmpty(agentId)) {
            CoreUserService userService = ApplicationContextHelper.getBean(CoreUserService.class);
            DingtalkUserService dingtalkUserService = ApplicationContextHelper.getBean(DingtalkUserService.class);
            String id = request.getParameter("id");
            if (!StringUtils.isEmpty(id)) {
                PasswordEncoder passwordEncoder = ApplicationContextHelper.getBean(PasswordEncoder.class);
                DefaultUserDetailsChecker.checkPassword(id, passwordEncoder.encode(request.getParameter("password")));
                boolean exist = false;
                DingtalkUserBean filter = new DingtalkUserBean();
                filter.setId(id);
                filter.setDtuserid(openId);
                filter.setAgentid(agentId);
                List<DingtalkUserBean> dingtalkUserList = dingtalkUserService.getDao().selectList((DingtalkUserBean)filter, new Order[0]);
                for (DingtalkUserBean dingtalkUser : dingtalkUserList) {
                    if (!(openId.equals(dingtalkUser.getDtuserid()) && id.equals(dingtalkUser.getId()) && agentId.equals(dingtalkUser.getAgentid()))) {
                        dingtalkUserService.delete(dingtalkUser.getId());
                        continue;
                    }
                    exist = true;
                }
                if (!exist) {
                    dingtalkUserService.insertPushUser(agentId, id, openId);
                }
                LoginUser loginUser = new LoginUser(id, request.getParameter("password"));
                loginUser.setAdditionalCheck(true);
                return loginUser;
            }
            DingtalkUserBean filter = new DingtalkUserBean();
            filter.setDtuserid(openId);
            filter.setAgentid(agentId);
            List<DingtalkUserBean> dingtalkUserList = dingtalkUserService.selectList(filter, new Order[0]);
            if (dingtalkUserList.size() == 1) {
                CoreUserBean user = (CoreUserBean)userService.selectByIdIfPresent(dingtalkUserList.get(0).getId());
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
        String agentId = request.getParameter("agentid");
        if (!StringUtils.isEmpty(openId)) {
            DingtalkUserService dingtalkUserService = ApplicationContextHelper.getBean(DingtalkUserService.class);
            DingtalkUserBean filter = new DingtalkUserBean();
            filter.setDtuserid(openId);
            filter.setAgentid(agentId);
            dingtalkUserService.getDao().deleteBy((DingtalkUserBean)filter, "OPENID", "AGENTID");
        }
    }
}
