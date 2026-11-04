/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.module.sys.bpmn.resource.impl;

import com.sunwayworld.framework.log.annotation.Log;
import com.sunwayworld.framework.log.annotation.LogModule;
import com.sunwayworld.framework.log.annotation.LogType;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import com.sunwayworld.module.sys.bpmn.bean.CoreBpmnInstanceGroupingCandidatorDTO;
import com.sunwayworld.module.sys.bpmn.resource.CoreBpmnInstanceResource;
import com.sunwayworld.module.sys.bpmn.service.CoreBpmnInstanceService;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
@LogModule(value="\u5de5\u4f5c\u6d41\u7a0b\u5b9e\u4f8b")
@GikamBean
public class CoreBpmnInstanceResourceImpl
implements CoreBpmnInstanceResource {
    @Autowired
    private CoreBpmnInstanceService instanceService;

    @Override
    public CoreBpmnInstanceService getService() {
        return this.instanceService;
    }

    @Override
    @Log(value="\u83b7\u53d6\u6d41\u7a0b\u6267\u884c\u5b9e\u4f8b\u4e2d\u6240\u6709\u5ba1\u6838\u4eba\u5458\u4fe1\u606f", type=LogType.SELECT)
    public List<CoreBpmnInstanceGroupingCandidatorDTO> selectInstanceGroupingCandidatorList(@PathVariable Long id) {
        return this.getService().selectInstanceGroupingCandidatorList(id);
    }
}
