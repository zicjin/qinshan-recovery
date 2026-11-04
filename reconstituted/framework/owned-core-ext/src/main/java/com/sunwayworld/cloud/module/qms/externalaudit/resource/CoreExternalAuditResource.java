/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.qms.externalaudit.resource;

import com.sunwayworld.cloud.module.qms.externalaudit.bean.CoreExternalAuditBean;
import com.sunwayworld.cloud.module.qms.externalaudit.bean.CoreExternalAuditGroupBean;
import com.sunwayworld.cloud.module.qms.externalaudit.bean.CoreExternalAuditNcrBean;
import com.sunwayworld.framework.data.page.Page;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.support.base.resource.GenericCloudResource;
import org.springframework.web.bind.annotation.RequestMapping;

@RequestMapping(value={"/secure/core/module/qms/external-audits"})
public interface CoreExternalAuditResource
extends GenericCloudResource<CoreExternalAuditBean, Long> {
    public Page<CoreExternalAuditGroupBean> selectAuditGroupPaginationByExternalAuditId(Long var1, RestJsonWrapperBean var2);

    public Long insertAuditGroup(Long var1, RestJsonWrapperBean var2);

    public void deleteAuditGroup(Long var1, RestJsonWrapperBean var2);

    public Page<CoreExternalAuditNcrBean> selectAuditNcrPaginationByExternalAuditId(Long var1, RestJsonWrapperBean var2);

    public Long insertAuditNcr(Long var1, RestJsonWrapperBean var2);

    public void deleteAuditNcr(Long var1, RestJsonWrapperBean var2);
}
