/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.qms.annualplan.service;

import com.sunwayworld.cloud.module.qms.annualplan.bean.CoreAnnualPlanBean;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.support.auditable.service.GenericAuditableService;
import com.sunwayworld.framework.support.base.service.GenericService;
import com.sunwayworld.framework.support.choosable.service.GenericChoosableService;
import com.sunwayworld.framework.support.selectable.service.GenericSelectableService;

public interface CoreAnnualPlanService
extends GenericService<CoreAnnualPlanBean, Long>,
GenericAuditableService<CoreAnnualPlanBean, Long>,
GenericChoosableService<CoreAnnualPlanBean, Long>,
GenericSelectableService<CoreAnnualPlanBean, Long> {
    public Long copyAnnualPlan(Long var1, RestJsonWrapperBean var2);
}
