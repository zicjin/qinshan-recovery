/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.module.sys.audit.dao;

import com.sunwayworld.framework.support.base.dao.GenericDao;
import com.sunwayworld.module.sys.audit.bean.CoreAuditLogColumnBean;
import com.sunwayworld.module.sys.audit.bean.CoreAuditLogColumnDTO;
import com.sunwayworld.module.sys.audit.bean.CoreAuditLogColumnNameDTO;
import java.util.List;

public interface CoreAuditLogColumnDao
extends GenericDao<CoreAuditLogColumnBean, Long> {
    public List<CoreAuditLogColumnNameDTO> selectDeletedNames(List<String> var1, String var2, String var3);

    public List<CoreAuditLogColumnDTO> selectColumnsByTargetInfo(String var1, String var2);
}
