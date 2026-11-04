/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.qms.dailysupervision.service;

import com.sunwayworld.cloud.module.qms.dailysupervision.bean.CoreDailySupervisionBean;
import com.sunwayworld.cloud.module.qms.dailysupervision.bean.CoreDailySupervisionConBean;
import com.sunwayworld.cloud.module.qms.dailysupervision.bean.CoreDailySupervisionItemBean;
import com.sunwayworld.framework.data.page.Page;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.support.auditable.service.GenericAuditableService;
import com.sunwayworld.framework.support.base.service.GenericService;

public interface CoreDailySupervisionService
extends GenericService<CoreDailySupervisionBean, Long>,
GenericAuditableService<CoreDailySupervisionBean, Long> {
    public Page<CoreDailySupervisionItemBean> selectSupervisionItemPaginationByDailySupervisionId(Long var1, RestJsonWrapperBean var2);

    public Long insertSupervisionItem(Long var1, RestJsonWrapperBean var2);

    public void deleteSupervisionItem(Long var1, RestJsonWrapperBean var2);

    public Page<CoreDailySupervisionConBean> selectSupervisionConPaginationByDailySupervisionId(Long var1, RestJsonWrapperBean var2);

    public Long insertSupervisionCon(Long var1, RestJsonWrapperBean var2);

    public void deleteSupervisionCon(Long var1, RestJsonWrapperBean var2);
}
