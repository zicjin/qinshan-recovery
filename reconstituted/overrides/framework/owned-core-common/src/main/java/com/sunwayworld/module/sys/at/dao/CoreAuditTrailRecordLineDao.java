/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.module.sys.at.dao;

import com.sunwayworld.framework.support.base.dao.GenericDao;
import com.sunwayworld.module.sys.at.bean.CoreAuditTrailRecordLineBean;
import com.sunwayworld.module.sys.at.bean.CoreAuditTrailRecordLineNameDTO;
import java.util.List;

public interface CoreAuditTrailRecordLineDao
extends GenericDao<CoreAuditTrailRecordLineBean, Long> {
    public List<String> selectTableNameByRecordId(String var1);

    public List<CoreAuditTrailRecordLineNameDTO> selectDeletedNames(List<String> var1, String var2);
    public List<CoreAuditTrailRecordLineNameDTO> selectDeletedNames(List<String> targetIds, String nameField, String organization);
}
