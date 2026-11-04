/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.qms.internalaudit.service;

import com.sunwayworld.cloud.module.qms.internalaudit.bean.CoreInternalAuditBean;
import com.sunwayworld.cloud.module.qms.internalaudit.bean.CoreInternalAuditGroupBean;
import com.sunwayworld.cloud.module.qms.internalaudit.bean.CoreInternalAuditNcrBean;
import com.sunwayworld.framework.data.page.Page;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.support.auditable.service.GenericAuditableService;
import com.sunwayworld.framework.support.base.service.GenericService;
import com.sunwayworld.framework.support.choosable.service.GenericChoosableService;

public interface CoreInternalAuditService
extends GenericService<CoreInternalAuditBean, Long>,
GenericAuditableService<CoreInternalAuditBean, Long>,
GenericChoosableService<CoreInternalAuditBean, Long> {
    public Page<CoreInternalAuditGroupBean> selectAuditGroupPaginationByInternalAuditId(Long var1, RestJsonWrapperBean var2);

    public void saveAuditGroup(Long var1, RestJsonWrapperBean var2);

    public void deleteAuditGroup(RestJsonWrapperBean var1);

    public Page<CoreInternalAuditNcrBean> selectAuditNcrPaginationByInternalAuditId(Long var1, RestJsonWrapperBean var2);

    public void saveAuditNcr(Long var1, RestJsonWrapperBean var2);

    public void deleteAuditNcr(RestJsonWrapperBean var1);
}
