/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.qms.managereview.service;

import com.sunwayworld.cloud.module.qms.managereview.bean.CoreManageReviewBean;
import com.sunwayworld.cloud.module.qms.managereview.bean.CoreManageReviewImproveBean;
import com.sunwayworld.framework.data.page.Page;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.support.auditable.service.GenericAuditableService;
import com.sunwayworld.framework.support.base.service.GenericService;
import com.sunwayworld.framework.support.selectable.service.GenericSelectableService;

public interface CoreManageReviewService
extends GenericService<CoreManageReviewBean, Long>,
GenericAuditableService<CoreManageReviewBean, Long>,
GenericSelectableService<CoreManageReviewBean, Long> {
    public Page<CoreManageReviewImproveBean> selectReviewImprovePaginationByManageReviewId(Long var1, RestJsonWrapperBean var2);

    public void saveReviewImprove(Long var1, RestJsonWrapperBean var2);

    public void deleteReviewImprove(RestJsonWrapperBean var1);
}
