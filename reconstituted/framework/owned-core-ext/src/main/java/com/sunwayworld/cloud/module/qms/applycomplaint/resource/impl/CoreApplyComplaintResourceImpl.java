/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.qms.applycomplaint.resource.impl;

import com.sunwayworld.cloud.module.qms.applycomplaint.bean.CoreApplyComplaintBean;
import com.sunwayworld.cloud.module.qms.applycomplaint.resource.CoreApplyComplaintResource;
import com.sunwayworld.cloud.module.qms.applycomplaint.service.CoreApplyComplaintService;
import com.sunwayworld.cloud.module.qms.applycomplaint.validator.CoreApplyComplaintCompleteValidator;
import com.sunwayworld.cloud.module.qms.applycomplaint.validator.CoreApplyComplaintStartValidator;
import com.sunwayworld.framework.log.annotation.Log;
import com.sunwayworld.framework.log.annotation.LogModule;
import com.sunwayworld.framework.log.annotation.LogType;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import com.sunwayworld.framework.support.auditable.resource.GenericAuditableResource;
import com.sunwayworld.framework.support.base.resource.AbstractGenericResource;
import com.sunwayworld.framework.validator.data.annotation.ValidateDataWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@LogModule(value="\u7533\u6295\u8bc9")
@RestController
@GikamBean
public class CoreApplyComplaintResourceImpl
implements CoreApplyComplaintResource,
AbstractGenericResource<CoreApplyComplaintService, CoreApplyComplaintBean, Long>,
GenericAuditableResource<CoreApplyComplaintService, CoreApplyComplaintBean, Long> {
    @Autowired
    private CoreApplyComplaintService coreApplyComplaintService;

    @Override
    public CoreApplyComplaintService getService() {
        return this.coreApplyComplaintService;
    }

    @Override
    @Log(value="\u65b0\u589e\u7533\u6295\u8bc9", type=LogType.INSERT)
    @RequestMapping(method={RequestMethod.POST})
    public Long insert(RestJsonWrapperBean wrapper) {
        return (Long)this.getService().insert(wrapper);
    }

    @Override
    @Log(value="\u5de5\u4f5c\u6d41\u542f\u52a8", type=LogType.PROCESS)
    @RequestMapping(value={"/action/start-process"}, method={RequestMethod.PUT})
    @ValidateDataWith(value=CoreApplyComplaintStartValidator.class)
    public void startProcess(RestJsonWrapperBean wrapper) {
        this.getService().startProcess(wrapper);
    }

    @Override
    @Log(value="\u5de5\u4f5c\u6d41\u5ba1\u6838\u901a\u8fc7", type=LogType.PROCESS)
    @RequestMapping(value={"/action/complete-task"}, method={RequestMethod.PUT})
    @ValidateDataWith(value=CoreApplyComplaintCompleteValidator.class)
    public void completeTask(RestJsonWrapperBean wrapper) {
        this.getService().completeTask(wrapper);
    }
}
