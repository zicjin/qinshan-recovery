/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.mobile.zwdd.sso;

import com.sunwayworld.cloud.module.mobile.zwdd.bean.ZwddUserBean;
import com.sunwayworld.cloud.module.mobile.zwdd.service.ZwddUserService;
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

public class ZwddSsoAuthenticator
implements SsoAuthenticator {
    @Override
    public LoginUser authenticate() {
        HttpServletRequest request = ServletUtils.getCurrentRequest();
        String openId = request.getParameter("openid");
        String agentId = request.getParameter("agentid");
        String tenantid = request.getParameter("tenantid");
        String accountid = request.getParameter("accountid");
        if (!(StringUtils.isEmpty(openId) || StringUtils.isEmpty(agentId) || StringUtils.isEmpty(tenantid) || StringUtils.isEmpty(accountid))) {
            CoreUserService userService = ApplicationContextHelper.getBean(CoreUserService.class);
            ZwddUserService dingtalkUserService = ApplicationContextHelper.getBean(ZwddUserService.class);
            String id = request.getParameter("id");
            if (!StringUtils.isEmpty(id)) {
                PasswordEncoder passwordEncoder = ApplicationContextHelper.getBean(PasswordEncoder.class);
                String _pwd = request.getParameter("password");
                DefaultUserDetailsChecker.checkPassword(id, passwordEncoder.encode(_pwd));
                boolean exist = false;
                ZwddUserBean filter = new ZwddUserBean();
                filter.setId(id);
                filter.setDtuserid(openId);
                filter.setAgentid(agentId);
                filter.setAccountid(accountid);
                filter.setTenantid(tenantid);
                List<ZwddUserBean> dingtalkUserList = dingtalkUserService.getDao().selectList((ZwddUserBean)filter, new Order[0]);
                for (ZwddUserBean dingtalkUser : dingtalkUserList) {
                    if (!(openId.equals(dingtalkUser.getDtuserid()) && id.equals(dingtalkUser.getId()) && agentId.equals(dingtalkUser.getAgentid()))) {
                        dingtalkUserService.delete(dingtalkUser.getId());
                        continue;
                    }
                    exist = true;
                }
                if (!exist) {
                    dingtalkUserService.insertPushUser(agentId, id, openId, tenantid, accountid);
                }
                LoginUser loginUser = new LoginUser(id, request.getParameter("password"));
                loginUser.setAdditionalCheck(true);
                return loginUser;
            }
            ZwddUserBean filter = new ZwddUserBean();
            filter.setDtuserid(openId);
            filter.setAgentid(agentId);
            filter.setTenantid(tenantid);
            filter.setAccountid(accountid);
            List<ZwddUserBean> dingtalkUserList = dingtalkUserService.selectList(filter, new Order[0]);
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
            ZwddUserService dingtalkUserService = ApplicationContextHelper.getBean(ZwddUserService.class);
            ZwddUserBean filter = new ZwddUserBean();
            filter.setDtuserid(openId);
            filter.setAgentid(agentId);
            dingtalkUserService.getDao().deleteBy((ZwddUserBean)filter, "OPENID", "AGENTID");
        }
    }
}
