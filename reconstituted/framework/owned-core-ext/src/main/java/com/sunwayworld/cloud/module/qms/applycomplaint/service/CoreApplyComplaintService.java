/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.qms.applycomplaint.service;

import com.sunwayworld.cloud.module.qms.applycomplaint.bean.CoreApplyComplaintBean;
import com.sunwayworld.framework.mybatis.mapper.MapperParameter;
import com.sunwayworld.framework.support.auditable.service.GenericAuditableService;
import com.sunwayworld.framework.support.base.service.GenericService;
import java.util.List;
import java.util.Map;

public interface CoreApplyComplaintService
extends GenericService<CoreApplyComplaintBean, Long>,
GenericAuditableService<CoreApplyComplaintBean, Long> {
    public List<Map<String, Object>> selectAuditStatusByCondition(MapperParameter var1);
}
