/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.qms.usertrain.service;

import com.sunwayworld.cloud.module.qms.usertrain.bean.CoreUserTrainBean;
import com.sunwayworld.framework.support.auditable.service.GenericAuditableService;
import com.sunwayworld.framework.support.base.service.GenericService;
import com.sunwayworld.framework.support.selectable.service.GenericSelectableService;

public interface CoreUserTrainService
extends GenericService<CoreUserTrainBean, Long>,
GenericAuditableService<CoreUserTrainBean, Long>,
GenericSelectableService<CoreUserTrainBean, Long> {
}
