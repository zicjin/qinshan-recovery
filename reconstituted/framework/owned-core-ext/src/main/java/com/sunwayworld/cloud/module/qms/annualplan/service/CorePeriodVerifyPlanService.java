/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.qms.annualplan.service;

import com.sunwayworld.cloud.module.qms.annualplan.bean.CorePeriodVerifyPlanBean;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.support.base.service.GenericService;
import com.sunwayworld.framework.support.choosable.service.GenericChoosableService;

public interface CorePeriodVerifyPlanService
extends GenericService<CorePeriodVerifyPlanBean, Long>,
GenericChoosableService<CorePeriodVerifyPlanBean, Long> {
    public void savePeriodVerifyPlan(Long var1, RestJsonWrapperBean var2);
}
