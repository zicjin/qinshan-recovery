/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.module.sys.audit.service;

import com.sunwayworld.framework.support.base.service.GenericService;
import com.sunwayworld.module.sys.audit.bean.CoreAuditLogColumnBean;
import com.sunwayworld.module.sys.audit.bean.CoreAuditLogColumnDTO;
import com.sunwayworld.module.sys.audit.bean.CoreAuditLogColumnNameDTO;
import java.util.List;

public interface CoreAuditLogColumnService
extends GenericService<CoreAuditLogColumnBean, Long> {
    public List<CoreAuditLogColumnNameDTO> selectDeletedNames(List<String> var1, String var2, String var3);

    public List<CoreAuditLogColumnDTO> selectColumnsByTargetInfo(String var1, String var2);
}
