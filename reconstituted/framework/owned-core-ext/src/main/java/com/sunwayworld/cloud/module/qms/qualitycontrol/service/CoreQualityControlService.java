/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.qms.qualitycontrol.service;

import com.sunwayworld.cloud.module.qms.qualitycontrol.bean.CoreQualityControlBean;
import com.sunwayworld.cloud.module.qms.qualitycontrol.bean.CoreQualityControlItemBean;
import com.sunwayworld.framework.data.page.Page;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.support.auditable.service.GenericAuditableService;
import com.sunwayworld.framework.support.base.service.GenericService;

public interface CoreQualityControlService
extends GenericService<CoreQualityControlBean, Long>,
GenericAuditableService<CoreQualityControlBean, Long> {
    public Page<CoreQualityControlItemBean> selectControlItemPaginationByQualityControlId(Long var1, RestJsonWrapperBean var2);

    public Long insertControlItem(Long var1, RestJsonWrapperBean var2);

    public void deleteControlItem(Long var1, RestJsonWrapperBean var2);
}
