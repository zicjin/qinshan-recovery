/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.framework.security.service;

import com.sunwayworld.framework.security.service.AbstractUserDetailsService;
import com.sunwayworld.module.mdm.user.bean.CoreUserBean;

public class DefaultUserDetailsService
extends AbstractUserDetailsService {
    @Override
    protected native CoreUserBean getLoginUser(String var1);
}
