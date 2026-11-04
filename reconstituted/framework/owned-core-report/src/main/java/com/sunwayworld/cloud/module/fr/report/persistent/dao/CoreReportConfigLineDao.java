/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.fr.report.persistent.dao;

import com.sunwayworld.cloud.module.fr.report.bean.CoreReportConfigLineBean;
import com.sunwayworld.framework.support.base.dao.GenericDao;
import java.util.List;
import java.util.Map;

public interface CoreReportConfigLineDao
extends GenericDao<CoreReportConfigLineBean, Long> {
    public List<Map<String, Object>> selectTreeList(Map<String, Object> var1);
}
