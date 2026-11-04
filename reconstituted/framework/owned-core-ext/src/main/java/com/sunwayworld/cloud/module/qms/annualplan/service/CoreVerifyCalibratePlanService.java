/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.qms.annualplan.service;

import com.sunwayworld.cloud.module.qms.annualplan.bean.CoreVerifyCalibratePlanBean;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.support.base.service.GenericService;
import com.sunwayworld.framework.support.choosable.service.GenericChoosableService;

public interface CoreVerifyCalibratePlanService
extends GenericService<CoreVerifyCalibratePlanBean, Long>,
GenericChoosableService<CoreVerifyCalibratePlanBean, Long> {
    public void saveVerifyCalibratePlan(Long var1, RestJsonWrapperBean var2);
}
