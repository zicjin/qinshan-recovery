/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.module.sys.at.dao;

import com.sunwayworld.framework.support.base.dao.GenericDao;
import com.sunwayworld.module.sys.at.bean.CoreAuditTraceOverviewDTO;
import com.sunwayworld.module.sys.at.bean.CoreAuditTrailRecordBean;
import java.util.List;

public interface CoreAuditTrailRecordDao
extends GenericDao<CoreAuditTrailRecordBean, Long> {
    public List<CoreAuditTraceOverviewDTO> selectTraceOverview(String var1, String var2, String var3);
}
