/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.train.exam.service;

import com.sunwayworld.cloud.module.train.exam.bean.CoreExamBean;
import com.sunwayworld.framework.support.activatable.service.GenericActivatableService;
import com.sunwayworld.framework.support.base.service.GenericService;
import com.sunwayworld.framework.support.choosable.service.GenericChoosableService;

public interface CoreExamService
extends GenericService<CoreExamBean, Long>,
GenericActivatableService<CoreExamBean, Long>,
GenericChoosableService<CoreExamBean, Long> {
}
