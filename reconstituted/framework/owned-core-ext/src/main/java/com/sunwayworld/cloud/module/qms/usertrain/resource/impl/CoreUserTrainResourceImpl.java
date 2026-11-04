/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.qms.usertrain.resource.impl;

import com.sunwayworld.cloud.module.qms.usertrain.bean.CoreUserTrainBean;
import com.sunwayworld.cloud.module.qms.usertrain.resource.CoreUserTrainResource;
import com.sunwayworld.cloud.module.qms.usertrain.service.CoreUserTrainService;
import com.sunwayworld.cloud.module.qms.usertrain.validator.CoreUserTrainCompleteTaskValidator;
import com.sunwayworld.cloud.module.qms.usertrain.validator.CoreUserTrainSubmitTaskValidator;
import com.sunwayworld.framework.log.annotation.Log;
import com.sunwayworld.framework.log.annotation.LogModule;
import com.sunwayworld.framework.log.annotation.LogType;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import com.sunwayworld.framework.support.auditable.resource.GenericAuditableResource;
import com.sunwayworld.framework.support.base.resource.AbstractGenericResource;
import com.sunwayworld.framework.support.selectable.resource.AbstractGenericSelectableResource;
import com.sunwayworld.framework.validator.data.annotation.ValidateDataWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@LogModule(value="\u4eba\u5458\u57f9\u8bad")
@RestController
@GikamBean
public class CoreUserTrainResourceImpl
implements CoreUserTrainResource,
GenericAuditableResource<CoreUserTrainService, CoreUserTrainBean, Long>,
AbstractGenericResource<CoreUserTrainService, CoreUserTrainBean, Long>,
AbstractGenericSelectableResource<CoreUserTrainService, CoreUserTrainBean, Long> {
    @Autowired
    private CoreUserTrainService coreUserTrainService;

    @Override
    public CoreUserTrainService getService() {
        return this.coreUserTrainService;
    }

    @Override
    @Log(value="\u65b0\u589e\u4eba\u5458\u57f9\u8bad", type=LogType.INSERT)
    @RequestMapping(method={RequestMethod.POST})
    public Long insert(RestJsonWrapperBean wrapper) {
        return (Long)this.getService().insert(wrapper);
    }

    @Override
    @Log(value="\u5de5\u4f5c\u6d41\u542f\u52a8", type=LogType.PROCESS)
    @RequestMapping(value={"/action/start-process"}, method={RequestMethod.PUT})
    @ValidateDataWith(value=CoreUserTrainSubmitTaskValidator.class)
    public void startProcess(RestJsonWrapperBean wrapper) {
        this.getService().startProcess(wrapper);
    }

    @Override
    @Log(value="\u5de5\u4f5c\u6d41\u5ba1\u6838\u901a\u8fc7", type=LogType.PROCESS)
    @RequestMapping(value={"/action/complete-task"}, method={RequestMethod.PUT})
    @ValidateDataWith(value=CoreUserTrainCompleteTaskValidator.class)
    public void completeTask(RestJsonWrapperBean wrapper) {
        this.getService().completeTask(wrapper);
    }
}
