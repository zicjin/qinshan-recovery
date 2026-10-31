package com.sunwayworld.emc.module.item.managereview.service;

import com.sunwayworld.emc.module.item.managereview.bean.EmcManageReviewBean;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.support.auditable.service.GenericAuditableService;
import com.sunwayworld.framework.support.base.service.GenericService;

public interface EmcManageReviewService extends GenericService<EmcManageReviewBean, Long>, GenericAuditableService<EmcManageReviewBean, Long> {
    Long insert(RestJsonWrapperBean wrapper);
}
