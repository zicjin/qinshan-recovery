/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.module.sys.bpmn.service;

import com.sunwayworld.framework.support.base.service.GenericService;
import com.sunwayworld.module.sys.bpmn.bean.CoreBpmnInstanceBean;
import com.sunwayworld.module.sys.bpmn.bean.CoreBpmnInstanceCandidatorDTO;
import com.sunwayworld.module.sys.bpmn.bean.CoreBpmnInstanceGroupingCandidatorDTO;
import com.sunwayworld.module.sys.bpmn.bean.CoreBpmnInstanceTaskCandidatorDTO;
import java.util.List;

public interface CoreBpmnInstanceService
extends GenericService<CoreBpmnInstanceBean, Long> {
    public List<CoreBpmnInstanceCandidatorDTO> selectInstanceCandidatorList(List<Long> var1);

    public List<CoreBpmnInstanceTaskCandidatorDTO> selectInstanceTaskAuditorList(List<Long> var1);

    public List<CoreBpmnInstanceGroupingCandidatorDTO> selectInstanceGroupingCandidatorList(Long var1);
}
