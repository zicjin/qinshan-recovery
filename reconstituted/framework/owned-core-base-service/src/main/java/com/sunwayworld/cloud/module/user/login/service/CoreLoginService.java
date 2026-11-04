/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.user.login.service;

import com.sunwayworld.cloud.module.user.bean.UserAccountDTO;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import org.springframework.lang.Nullable;

public interface CoreLoginService {
    public UserAccountDTO getAccount(String var1);

    public boolean checkAccount(RestJsonWrapperBean var1);

    public void updateLoginInfo(String var1, @Nullable Long var2);
}
