/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.module.sys.at.service;

import com.sunwayworld.framework.support.base.service.GenericService;
import com.sunwayworld.module.sys.at.bean.CoreAuditTrailRecordLineBean;
import com.sunwayworld.module.sys.at.bean.CoreAuditTrailRecordLineNameDTO;
import java.util.List;

public interface CoreAuditTrailRecordLineService
extends GenericService<CoreAuditTrailRecordLineBean, Long> {
    public void insert(List<CoreAuditTrailRecordLineBean> var1);

    public List<CoreAuditTrailRecordLineBean> selectRecordLineTableNameList(Long var1);

    public List<CoreAuditTrailRecordLineBean> selectRecordLineList(Long var1, String var2);

    public List<CoreAuditTrailRecordLineNameDTO> selectDeletedNames(List<String> var1, String var2);
}
