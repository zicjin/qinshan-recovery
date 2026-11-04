/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.train.trainplan.service;

import com.sunwayworld.cloud.module.train.trainplan.bean.CoreTrainPlanBean;
import com.sunwayworld.framework.support.auditable.service.GenericAuditableService;
import com.sunwayworld.framework.support.base.service.GenericService;
import com.sunwayworld.framework.support.choosable.service.GenericChoosableService;

public interface CoreTrainPlanService
extends GenericService<CoreTrainPlanBean, Long>,
GenericAuditableService<CoreTrainPlanBean, Long>,
GenericChoosableService<CoreTrainPlanBean, Long> {
}
