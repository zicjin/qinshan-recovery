/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.framework.security.sso.impl;

import com.sunwayworld.framework.cache.redis.RedisHelper;
import com.sunwayworld.framework.context.ApplicationContextHelper;
import com.sunwayworld.framework.database.sql.Order;
import com.sunwayworld.framework.mybatis.mapper.MatchPattern;
import com.sunwayworld.framework.mybatis.mapper.SearchFilter;
import com.sunwayworld.framework.security.bean.LoginUser;
import com.sunwayworld.framework.security.sso.SsoAuthenticator;
import com.sunwayworld.framework.utils.NumberUtils;
import com.sunwayworld.framework.utils.ServletUtils;
import com.sunwayworld.framework.utils.StringUtils;
import com.sunwayworld.module.mdm.user.bean.CoreUserBean;
import com.sunwayworld.module.mdm.user.service.CoreUserService;
import javax.servlet.http.HttpServletRequest;

public class SmsLoginSsoAuthenticator
implements SsoAuthenticator {
    @Override
    public LoginUser authenticate() {
        HttpServletRequest request = ServletUtils.getCurrentRequest();
        String mobile = request.getParameter("mobile");
        String smsCode = request.getParameter("smsCode");
        String roleId = request.getParameter("roleId");
        String orgId = request.getParameter("orgId");
        if (smsCode.equals(RedisHelper.get("SMS_LOGIN_CACHE", mobile))) {
            CoreUserService userService = ApplicationContextHelper.getBean(CoreUserService.class);
            CoreUserBean user = (CoreUserBean)userService.selectFirstByFilter(SearchFilter.instance().match("MOBILE", mobile).filter(MatchPattern.EQ), Order.desc("ID"));
            LoginUser loginUser = new LoginUser(user.getId(), user.getPassword());
            if (!StringUtils.isBlank(roleId)) {
                loginUser.setRoleId(NumberUtils.parseLong(roleId));
            }
            if (!StringUtils.isBlank(orgId)) {
                loginUser.setOrgId(orgId);
            }
            return loginUser;
        }
        return null;
    }
}
