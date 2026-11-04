/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.qms.annualplan.service;

import com.sunwayworld.cloud.module.qms.annualplan.bean.CoreEquFuncCheckPlanBean;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.support.base.service.GenericService;
import com.sunwayworld.framework.support.choosable.service.GenericChoosableService;

public interface CoreEquFuncCheckPlanService
extends GenericService<CoreEquFuncCheckPlanBean, Long>,
GenericChoosableService<CoreEquFuncCheckPlanBean, Long> {
    public void saveEquFuncCheckPlan(Long var1, RestJsonWrapperBean var2);
}
