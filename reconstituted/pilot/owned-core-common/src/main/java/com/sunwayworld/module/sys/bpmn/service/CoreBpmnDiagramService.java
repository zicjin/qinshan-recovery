/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.module.sys.bpmn.service;

import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.support.base.service.GenericService;
import com.sunwayworld.module.sys.bpmn.bean.CoreBpmnDiagramBean;
import com.sunwayworld.module.sys.bpmn.bean.CoreBpmnDiagramSequenceFlowConditionDTO;
import com.sunwayworld.module.sys.bpmn.bean.CoreBpmnDiagramTaskCandidatorDTO;
import java.util.List;
import org.dom4j.Document;

public interface CoreBpmnDiagramService
extends GenericService<CoreBpmnDiagramBean, Long> {
    public Document getDocument(Long var1);

    public List<CoreBpmnDiagramTaskCandidatorDTO> selectTaskCandidatorList(Long var1, RestJsonWrapperBean var2);

    public List<CoreBpmnDiagramSequenceFlowConditionDTO> selectSequenceFlowConditionList(Long var1);
}
