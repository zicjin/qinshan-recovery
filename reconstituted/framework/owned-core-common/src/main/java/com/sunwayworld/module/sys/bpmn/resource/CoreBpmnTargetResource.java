/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.module.sys.bpmn.resource;

import com.sunwayworld.framework.log.annotation.Log;
import com.sunwayworld.framework.log.annotation.LogType;
import com.sunwayworld.framework.support.base.resource.GenericResource;
import com.sunwayworld.module.sys.bpmn.bean.CoreBpmnTargetBean;
import com.sunwayworld.module.sys.bpmn.service.CoreBpmnTargetService;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@RequestMapping(value={"/secure/core/module/sys/bpmn-targets"})
public interface CoreBpmnTargetResource
extends GenericResource<CoreBpmnTargetService, CoreBpmnTargetBean, String> {
    @Log(value="\u83b7\u53d6\u4f4e\u4ee3\u7801service\u8def\u5f84", type=LogType.EXPORT)
    @RequestMapping(value={"/{tableName}/lcdp-url/queries"}, method={RequestMethod.GET})
    public String getLcdpUrl(@PathVariable String var1);
}
