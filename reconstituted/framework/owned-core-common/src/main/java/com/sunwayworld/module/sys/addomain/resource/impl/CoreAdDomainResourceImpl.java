/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.module.sys.addomain.resource.impl;

import com.sunwayworld.framework.log.annotation.Log;
import com.sunwayworld.framework.log.annotation.LogType;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.restful.data.RestValidationResultBean;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import com.sunwayworld.module.sys.addomain.resource.CoreAdDomainResource;
import com.sunwayworld.module.sys.addomain.service.CoreAdDomainUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

@GikamBean
@RestController
public class CoreAdDomainResourceImpl
implements CoreAdDomainResource {
    @Autowired
    private CoreAdDomainUserService coreAdDomainUserService;

    @Override
    @Log(value="\u65b0\u589e\u6216\u4fee\u6539AD\u57df\u7528\u6237", type=LogType.INSERT)
    public void insertAdDomainUser(RestJsonWrapperBean wrapper) {
        this.coreAdDomainUserService.insert(wrapper);
    }

    @Override
    @Log(value="\u6821\u9a8cAD\u57df\u7528\u6237", type=LogType.VALIDATE)
    public RestValidationResultBean validateAdDomainUserUnique(String id, String columnName, String columnValue) {
        return this.coreAdDomainUserService.validateUnique(id, columnName, columnValue);
    }
}
