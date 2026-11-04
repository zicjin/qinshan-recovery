/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.module.sys.password.resource.impl;

import com.sunwayworld.framework.log.annotation.Log;
import com.sunwayworld.framework.log.annotation.LogModule;
import com.sunwayworld.framework.log.annotation.LogType;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import com.sunwayworld.framework.validator.data.annotation.ValidateDataWith;
import com.sunwayworld.module.sys.password.bean.CorePasswordPolicyBean;
import com.sunwayworld.module.sys.password.resource.CorePasswordPolicyResource;
import com.sunwayworld.module.sys.password.service.CorePasswordPolicyService;
import com.sunwayworld.module.sys.password.validator.CorePasswordPolicyDeleteValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

@LogModule(value="\u5bc6\u7801\u7b56\u7565")
@RestController
@GikamBean
public class CorePasswordPolicyResourceImpl
implements CorePasswordPolicyResource {
    @Autowired
    private CorePasswordPolicyService policyService;

    @Override
    public CorePasswordPolicyService getService() {
        return this.policyService;
    }

    @Override
    public Long insert(RestJsonWrapperBean jsonWrapper) {
        return this.getService().insert(jsonWrapper);
    }

    @Override
    @ValidateDataWith(value=CorePasswordPolicyDeleteValidator.class)
    public void delete(RestJsonWrapperBean wrapper) {
        this.getService().delete(wrapper);
    }

    @Override
    @Log(value="\u83b7\u53d6\u5339\u914d\u5f53\u524d\u7528\u6237\u7684\u79d8\u5bc6\u7b56\u7565", type=LogType.SELECT)
    public CorePasswordPolicyBean selectMatchingPasswordPolicy() {
        return this.getService().selectMatchingPasswordPolicy();
    }
}
