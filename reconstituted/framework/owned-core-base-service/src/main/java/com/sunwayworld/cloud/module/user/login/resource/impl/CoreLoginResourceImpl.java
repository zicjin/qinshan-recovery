/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.user.login.resource.impl;

import com.sunwayworld.cloud.module.user.bean.UserAccountDTO;
import com.sunwayworld.cloud.module.user.login.resource.CoreLoginResource;
import com.sunwayworld.cloud.module.user.login.service.CoreLoginService;
import com.sunwayworld.framework.context.LocalContextHelper;
import com.sunwayworld.framework.log.annotation.Log;
import com.sunwayworld.framework.log.annotation.LogModule;
import com.sunwayworld.framework.log.annotation.LogType;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@LogModule(value="\u7528\u6237\u767b\u5f55")
@RestController
@GikamBean
@RequestMapping(value={"/core/module/sys/logins"})
public class CoreLoginResourceImpl
implements CoreLoginResource {
    @Autowired
    private CoreLoginService loginService;

    @Override
    @Log(value="\u67e5\u8be2\u5f53\u524d\u7528\u6237\u767b\u5f55\u4fe1\u606f", type=LogType.SELECT)
    public UserAccountDTO getAccount() {
        return this.loginService.getAccount(LocalContextHelper.getLoginUserId());
    }

    @Override
    @Log(value="\u67e5\u8be2\u5f53\u524d\u7528\u6237\u767b\u5f55\u4fe1\u606f", type=LogType.SELECT)
    public UserAccountDTO getAccount(String userId) {
        return this.loginService.getAccount(userId);
    }

    @Override
    @Log(value="\u9a8c\u8bc1\u7528\u6237\u767b\u5f55\u7684\u5e10\u53f7\u548c\u5bc6\u7801\u662f\u5426\u5339\u914d", type=LogType.SELECT)
    public boolean checkAccount(RestJsonWrapperBean wrapper) {
        return this.loginService.checkAccount(wrapper);
    }

    @Override
    @Log(value="\u66f4\u65b0\u7528\u6237\u767b\u5f55\u4fe1\u606f", type=LogType.UPDATE)
    public void updateLoginInfo(@RequestParam(value="orgId") String orgId, @RequestParam(name="roleId", required=false) Long roleId) {
        this.loginService.updateLoginInfo(orgId, roleId);
    }
}
