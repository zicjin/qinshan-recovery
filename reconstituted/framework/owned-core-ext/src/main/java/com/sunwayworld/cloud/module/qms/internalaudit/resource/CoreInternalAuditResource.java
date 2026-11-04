/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.qms.internalaudit.resource;

import com.sunwayworld.cloud.module.qms.internalaudit.bean.CoreInternalAuditBean;
import com.sunwayworld.cloud.module.qms.internalaudit.bean.CoreInternalAuditGroupBean;
import com.sunwayworld.cloud.module.qms.internalaudit.bean.CoreInternalAuditNcrBean;
import com.sunwayworld.framework.data.page.Page;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.support.base.resource.GenericCloudResource;
import org.springframework.web.bind.annotation.RequestMapping;

@RequestMapping(value={"/secure/core/module/qms/internal-audits"})
public interface CoreInternalAuditResource
extends GenericCloudResource<CoreInternalAuditBean, Long> {
    public Page<CoreInternalAuditGroupBean> selectAuditGroupPaginationByInternalAuditId(Long var1, RestJsonWrapperBean var2);

    public void saveAuditGroup(Long var1, RestJsonWrapperBean var2);

    public void deleteAuditGroup(RestJsonWrapperBean var1);

    public Page<CoreInternalAuditNcrBean> selectAuditNcrPaginationByInternalAuditId(Long var1, RestJsonWrapperBean var2);

    public void saveAuditNcr(Long var1, RestJsonWrapperBean var2);

    public void deleteAuditNcr(RestJsonWrapperBean var1);
}
