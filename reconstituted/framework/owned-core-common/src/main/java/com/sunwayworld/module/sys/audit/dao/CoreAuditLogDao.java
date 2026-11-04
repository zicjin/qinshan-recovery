/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.module.sys.audit.dao;

import com.sunwayworld.framework.support.base.dao.GenericDao;
import com.sunwayworld.module.sys.audit.bean.CoreAuditLogBean;
import com.sunwayworld.module.sys.audit.bean.CoreAuditLogTraceOverviewDTO;
import java.util.List;

public interface CoreAuditLogDao
extends GenericDao<CoreAuditLogBean, Long> {
    public List<CoreAuditLogTraceOverviewDTO> selectTraceOverview(String var1, String var2, String var3);
}
