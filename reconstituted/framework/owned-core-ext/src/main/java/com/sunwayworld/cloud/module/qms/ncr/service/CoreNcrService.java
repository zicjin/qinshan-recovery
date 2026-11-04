/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.qms.ncr.service;

import com.sunwayworld.cloud.module.qms.ncr.bean.CoreNcrBean;
import com.sunwayworld.framework.support.auditable.service.GenericAuditableService;
import com.sunwayworld.framework.support.base.service.GenericService;
import com.sunwayworld.framework.support.choosable.service.GenericChoosableService;

public interface CoreNcrService
extends GenericService<CoreNcrBean, Long>,
GenericAuditableService<CoreNcrBean, Long>,
GenericChoosableService<CoreNcrBean, Long> {
}
