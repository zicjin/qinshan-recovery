/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.module.sys.bpmn.resource;

import com.sunwayworld.framework.data.page.Page;
import com.sunwayworld.framework.log.annotation.Log;
import com.sunwayworld.framework.log.annotation.LogType;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.support.base.resource.GenericResource;
import com.sunwayworld.module.sys.bpmn.bean.CoreBpmnDiagramBean;
import com.sunwayworld.module.sys.bpmn.bean.CoreBpmnDiagramSequenceFlowConditionDTO;
import com.sunwayworld.module.sys.bpmn.bean.CoreBpmnDiagramTaskCandidatorDTO;
import com.sunwayworld.module.sys.bpmn.service.CoreBpmnDiagramService;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@RequestMapping(value={"/secure/core/module/sys/bpmn-diagrams"})
public interface CoreBpmnDiagramResource
extends GenericResource<CoreBpmnDiagramService, CoreBpmnDiagramBean, Long> {
    @Log(value="\u83b7\u53d6\u6d41\u7a0b\u56fe\u8868\u4e2d\u6240\u6709\u8282\u70b9\u5ba1\u6838\u4eba\u5458\u4fe1\u606f", type=LogType.SELECT)
    @RequestMapping(value={"/{id}/queries/task-candidators"}, method={RequestMethod.POST})
    public Page<CoreBpmnDiagramTaskCandidatorDTO> selectTaskCandidatorList(@PathVariable Long var1, RestJsonWrapperBean var2);

    @Log(value="\u83b7\u53d6\u6d41\u7a0b\u56fe\u8868\u4e2d\u6240\u6709\u7ebf\u4e2d\u7684\u6761\u4ef6\u4fe1\u606f", type=LogType.SELECT)
    @RequestMapping(value={"/{id}/queries/sequence-flow-conditions"}, method={RequestMethod.POST})
    public Page<CoreBpmnDiagramSequenceFlowConditionDTO> selectSequenceFlowConditionList(@PathVariable Long var1, RestJsonWrapperBean var2);
}
