/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.module.sys.bpmn.resource.impl;

import com.sunwayworld.framework.log.annotation.LogModule;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import com.sunwayworld.module.sys.bpmn.resource.CoreBpmnTargetResource;
import com.sunwayworld.module.sys.bpmn.service.CoreBpmnTargetService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

@GikamBean
@RestController
@LogModule(value="\u6d41\u7a0b\u5bf9\u5e94\u7684\u4e1a\u52a1\u4fe1\u606f")
public class CoreBpmnTargetResourceImpl
implements CoreBpmnTargetResource {
    @Autowired
    private CoreBpmnTargetService targetService;

    @Override
    public CoreBpmnTargetService getService() {
        return this.targetService;
    }

    @Override
    public String getLcdpUrl(String tableName) {
        return this.getService().getLcdpUrl(tableName);
    }
}
