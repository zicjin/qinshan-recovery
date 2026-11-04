/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.module.sys.audit.service;

import com.sunwayworld.framework.data.page.Page;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.support.base.service.GenericService;
import com.sunwayworld.module.sys.audit.bean.CoreAuditLogActivityBean;

public interface CoreAuditLogActivityService
extends GenericService<CoreAuditLogActivityBean, Long> {
    public Page<CoreAuditLogActivityBean> selectQueryMorePagination(RestJsonWrapperBean var1);
}
