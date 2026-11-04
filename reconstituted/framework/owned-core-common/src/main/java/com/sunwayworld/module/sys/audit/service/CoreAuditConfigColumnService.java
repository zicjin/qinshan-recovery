/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.module.sys.audit.service;

import com.sunwayworld.framework.support.base.service.GenericService;
import com.sunwayworld.module.sys.audit.bean.CoreAuditConfigColumnBean;
import java.util.List;

public interface CoreAuditConfigColumnService
extends GenericService<CoreAuditConfigColumnBean, Long> {
    public List<CoreAuditConfigColumnBean> selectListByConfigId(String var1);

    public List<CoreAuditConfigColumnBean> selectAuditableListByConfigId(String var1);
}
