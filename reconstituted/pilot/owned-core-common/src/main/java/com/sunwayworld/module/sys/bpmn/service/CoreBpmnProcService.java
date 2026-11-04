/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.module.sys.bpmn.service;

import com.sunwayworld.framework.data.page.Page;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.support.base.service.GenericService;
import com.sunwayworld.framework.support.choosable.service.GenericChoosableService;
import com.sunwayworld.module.sys.bpmn.bean.CoreBpmnProcBean;
import com.sunwayworld.module.sys.bpmn.bean.CoreBpmnProcOrgBean;
import java.util.List;

public interface CoreBpmnProcService
extends GenericService<CoreBpmnProcBean, Long>,
GenericChoosableService<CoreBpmnProcBean, Long> {
    public List<CoreBpmnProcBean> selectRuntimeBpmnProcList(String var1, String var2);

    public List<CoreBpmnProcBean> selectBpmnProcListByServiceId(String var1);

    public CoreBpmnProcBean selectLatestProc(String var1);

    public String getBpmnInputTextUrl(Long var1);

    public String getBpmnInputTextUrl(RestJsonWrapperBean var1);

    public Page<CoreBpmnProcOrgBean> selectProcOrgPagination(Long var1, RestJsonWrapperBean var2);
}
