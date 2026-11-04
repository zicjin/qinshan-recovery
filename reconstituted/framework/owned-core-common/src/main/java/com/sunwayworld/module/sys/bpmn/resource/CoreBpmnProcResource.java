/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.module.sys.bpmn.resource;

import com.sunwayworld.framework.data.page.Page;
import com.sunwayworld.framework.log.annotation.Log;
import com.sunwayworld.framework.log.annotation.LogType;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.support.base.resource.GenericResource;
import com.sunwayworld.framework.support.choosable.resource.GenericChoosableResource;
import com.sunwayworld.module.sys.bpmn.bean.CoreBpmnProcBean;
import com.sunwayworld.module.sys.bpmn.bean.CoreBpmnProcOrgBean;
import com.sunwayworld.module.sys.bpmn.service.CoreBpmnProcService;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@RequestMapping(value={"/secure/core/module/sys/bpmn-procs"})
public interface CoreBpmnProcResource
extends GenericResource<CoreBpmnProcService, CoreBpmnProcBean, Long>,
GenericChoosableResource<CoreBpmnProcService, CoreBpmnProcBean, Long> {
    @Log(value="\u83b7\u53d6\u6d41\u7a0b\u56fe\u5bfc\u5165\u6587\u4ef6\u7684\u5730\u5740", type=LogType.EXPORT)
    @RequestMapping(value={"/{id}/bpmn-input-url"}, method={RequestMethod.POST})
    public String getBpmnInputTextUrl(@PathVariable Long var1);

    @Log(value="\u6279\u91cf\u83b7\u53d6\u6d41\u7a0b\u56fe\u5bfc\u5165\u6587\u4ef6\u7684\u5730\u5740", type=LogType.EXPORT)
    @RequestMapping(value={"/bpmn-input-url"}, method={RequestMethod.POST})
    public String getBpmnInputTextUrl(RestJsonWrapperBean var1);

    @RequestMapping(value={"/{id}/bpmn-proc-orgs/queries"}, method={RequestMethod.POST})
    public Page<CoreBpmnProcOrgBean> selectProcOrgPagination(Long var1, RestJsonWrapperBean var2);
}
