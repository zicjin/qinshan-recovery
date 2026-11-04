/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.framework.security.enhance.impl;

import com.sunwayworld.framework.security.bean.LoginUser;
import com.sunwayworld.framework.security.enhance.LoginUserEnhancer;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import org.springframework.stereotype.Repository;

@GikamBean
@Repository
public class DefaultLoginUserEnhancer
implements LoginUserEnhancer {
    @Override
    public void enhance(LoginUser loginUser) {
    }
}
