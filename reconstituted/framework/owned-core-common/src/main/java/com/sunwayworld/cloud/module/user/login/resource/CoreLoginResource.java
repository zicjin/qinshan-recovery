/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.user.login.resource;

import com.sunwayworld.cloud.module.user.bean.UserAccountDTO;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

public interface CoreLoginResource {
    @RequestMapping(value={"/account"}, method={RequestMethod.GET})
    public UserAccountDTO getAccount();

    @RequestMapping(value={"/account/{userId}"}, method={RequestMethod.GET})
    public UserAccountDTO getAccount(@PathVariable String var1);

    @RequestMapping(value={"/action/check-account"}, method={RequestMethod.POST})
    public boolean checkAccount(RestJsonWrapperBean var1);

    @RequestMapping(method={RequestMethod.PUT})
    public void updateLoginInfo(@RequestParam String var1, @RequestParam(required=false) Long var2);
}
