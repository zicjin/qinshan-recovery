/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.module.sys.bpmn.resource.impl;

import com.sunwayworld.framework.data.page.Page;
import com.sunwayworld.framework.data.page.Pagination;
import com.sunwayworld.framework.log.annotation.LogModule;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import com.sunwayworld.module.sys.bpmn.bean.CoreBpmnDiagramSequenceFlowConditionDTO;
import com.sunwayworld.module.sys.bpmn.bean.CoreBpmnDiagramTaskCandidatorDTO;
import com.sunwayworld.module.sys.bpmn.resource.CoreBpmnDiagramResource;
import com.sunwayworld.module.sys.bpmn.service.CoreBpmnDiagramService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
@LogModule(value="\u5de5\u4f5c\u6d41\u56fe\u8868\u529f\u80fd")
@GikamBean
public class CoreBpmnDiagramResourceImpl
implements CoreBpmnDiagramResource {
    @Autowired
    private CoreBpmnDiagramService diagramService;

    @Override
    public CoreBpmnDiagramService getService() {
        return this.diagramService;
    }

    @Override
    public Page<CoreBpmnDiagramTaskCandidatorDTO> selectTaskCandidatorList(@PathVariable Long id, RestJsonWrapperBean wrapper) {
        return new Pagination<CoreBpmnDiagramTaskCandidatorDTO>(this.getService().selectTaskCandidatorList(id, wrapper));
    }

    @Override
    public Page<CoreBpmnDiagramSequenceFlowConditionDTO> selectSequenceFlowConditionList(@PathVariable Long id, RestJsonWrapperBean wrapper) {
        return new Pagination<CoreBpmnDiagramSequenceFlowConditionDTO>(this.getService().selectSequenceFlowConditionList(id));
    }
}
