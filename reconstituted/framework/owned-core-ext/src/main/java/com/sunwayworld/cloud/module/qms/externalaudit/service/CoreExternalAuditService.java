/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.qms.externalaudit.service;

import com.sunwayworld.cloud.module.qms.externalaudit.bean.CoreExternalAuditBean;
import com.sunwayworld.cloud.module.qms.externalaudit.bean.CoreExternalAuditGroupBean;
import com.sunwayworld.cloud.module.qms.externalaudit.bean.CoreExternalAuditNcrBean;
import com.sunwayworld.framework.data.page.Page;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.support.auditable.service.GenericAuditableService;
import com.sunwayworld.framework.support.base.service.GenericService;
import com.sunwayworld.framework.support.choosable.service.GenericChoosableService;

public interface CoreExternalAuditService
extends GenericService<CoreExternalAuditBean, Long>,
GenericAuditableService<CoreExternalAuditBean, Long>,
GenericChoosableService<CoreExternalAuditBean, Long> {
    public Page<CoreExternalAuditGroupBean> selectAuditGroupPaginationByExternalAuditId(Long var1, RestJsonWrapperBean var2);

    public Long insertAuditGroup(Long var1, RestJsonWrapperBean var2);

    public void deleteAuditGroup(Long var1, RestJsonWrapperBean var2);

    public Page<CoreExternalAuditNcrBean> selectAuditNcrPaginationByExternalAuditId(Long var1, RestJsonWrapperBean var2);

    public Long insertAuditNcr(Long var1, RestJsonWrapperBean var2);

    public void deleteAuditNcr(Long var1, RestJsonWrapperBean var2);
}
