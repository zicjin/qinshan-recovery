/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.framework.security.sso.impl;

import com.sunwayworld.framework.cache.redis.RedisHelper;
import com.sunwayworld.framework.context.ApplicationContextHelper;
import com.sunwayworld.framework.context.SunwayAopContext;
import com.sunwayworld.framework.database.sql.Order;
import com.sunwayworld.framework.mybatis.mapper.MatchPattern;
import com.sunwayworld.framework.mybatis.mapper.SearchFilter;
import com.sunwayworld.framework.security.bean.LoginUser;
import com.sunwayworld.framework.security.sso.SsoAuthenticator;
import com.sunwayworld.framework.utils.StringUtils;
import com.sunwayworld.module.mdm.user.bean.CoreUserBean;
import com.sunwayworld.module.mdm.user.service.CoreUserService;
import org.springframework.security.crypto.password.PasswordEncoder;

public class TestLoginSsoAuthenticator
implements SsoAuthenticator {
    @Override
    public LoginUser authenticate() {
        String username = (String)SunwayAopContext.getCache("username");
        String password = (String)SunwayAopContext.getCache("password");
        String mobile = (String)SunwayAopContext.getCache("mobile");
        String smscode = (String)SunwayAopContext.getCache("smscode");
        CoreUserService userService = ApplicationContextHelper.getBean(CoreUserService.class);
        CoreUserBean user = null;
        if (!StringUtils.isBlank(username) && !StringUtils.isBlank(password)) {
            user = userService.selectByLoginId(username);
            PasswordEncoder passwordEncoder = ApplicationContextHelper.getBean(PasswordEncoder.class);
            if (user != null && user.getPassword().equals(passwordEncoder.encode(password))) {
                return new LoginUser(user.getId(), user.getPassword());
            }
        } else if (!StringUtils.isBlank(mobile) && !StringUtils.isBlank(smscode) && (user = (CoreUserBean)userService.selectFirstByFilter(SearchFilter.instance().match("MOBILE", mobile).filter(MatchPattern.EQ), Order.desc("ID"))) != null && smscode.equals(RedisHelper.get("SMS_LOGIN_CACHE", mobile))) {
            return new LoginUser(user.getId(), user.getPassword());
        }
        return null;
    }

    @Override
    public boolean login() {
        return false;
    }
}
