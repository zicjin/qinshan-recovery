/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.qms.managereview.resource;

import com.sunwayworld.cloud.module.qms.managereview.bean.CoreManageReviewBean;
import com.sunwayworld.cloud.module.qms.managereview.bean.CoreManageReviewImproveBean;
import com.sunwayworld.framework.data.page.Page;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.support.base.resource.GenericCloudResource;
import com.sunwayworld.framework.support.selectable.resource.GenericSelectableCloudResource;
import org.springframework.web.bind.annotation.RequestMapping;

@RequestMapping(value={"/secure/core/module/qms/manage-reviews"})
public interface CoreManageReviewResource
extends GenericCloudResource<CoreManageReviewBean, Long>,
GenericSelectableCloudResource<CoreManageReviewBean, Long> {
    public Page<CoreManageReviewImproveBean> selectReviewImprovePaginationByManageReviewId(Long var1, RestJsonWrapperBean var2);

    public void saveReviewImprove(Long var1, RestJsonWrapperBean var2);

    public void deleteReviewImprove(RestJsonWrapperBean var1);
}
