package com.sunwayworld.framework.security.service;

import com.sunwayworld.module.mdm.user.bean.CoreUserBean;
import com.sunwayworld.module.mdm.user.service.CoreUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;

public class DefaultUserDetailsService extends AbstractUserDetailsService {
    @Autowired
    @Lazy
    private CoreUserService userService;

    @Override
    protected CoreUserBean getLoginUser(String loginId) {
        return this.userService.selectByLoginId(loginId);
    }
}
