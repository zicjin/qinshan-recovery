/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.module.sys.bpmn.resource;

import com.sunwayworld.framework.log.annotation.Log;
import com.sunwayworld.framework.log.annotation.LogType;
import com.sunwayworld.framework.support.base.resource.GenericResource;
import com.sunwayworld.module.sys.bpmn.bean.CoreBpmnInstanceBean;
import com.sunwayworld.module.sys.bpmn.bean.CoreBpmnInstanceGroupingCandidatorDTO;
import com.sunwayworld.module.sys.bpmn.service.CoreBpmnInstanceService;
import java.util.List;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@RequestMapping(value={"/secure/core/module/sys/bpmn-insts"})
public interface CoreBpmnInstanceResource
extends GenericResource<CoreBpmnInstanceService, CoreBpmnInstanceBean, Long> {
    @Log(value="\u83b7\u53d6\u6d41\u7a0b\u6267\u884c\u5b9e\u4f8b\u4e2d\u6240\u6709\u5ba1\u6838\u4eba\u5458\u4fe1\u606f", type=LogType.SELECT)
    @RequestMapping(value={"/{id}/queries/grouping-candidators"}, method={RequestMethod.GET})
    public List<CoreBpmnInstanceGroupingCandidatorDTO> selectInstanceGroupingCandidatorList(@PathVariable Long var1);
}
