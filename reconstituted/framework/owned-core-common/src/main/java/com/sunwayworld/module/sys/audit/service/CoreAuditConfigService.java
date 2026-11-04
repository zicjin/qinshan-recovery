/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.module.sys.audit.service;

import com.sunwayworld.framework.data.page.Page;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.support.base.service.GenericService;
import com.sunwayworld.framework.support.selectable.service.GenericSelectableService;
import com.sunwayworld.module.sys.audit.bean.CoreAuditConfigBean;
import com.sunwayworld.module.sys.audit.bean.CoreAuditConfigColumnBean;

public interface CoreAuditConfigService
extends GenericService<CoreAuditConfigBean, String>,
GenericSelectableService<CoreAuditConfigBean, String> {
    public void init();

    public Page<CoreAuditConfigColumnBean> selectConfigColumnPaginationById(String var1);

    public void initConfigColumn(String var1);

    public void enableConfigColumn(String var1, RestJsonWrapperBean var2);

    public void disableConfigColumn(String var1, RestJsonWrapperBean var2);
}
