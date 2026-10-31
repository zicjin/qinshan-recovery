package com.sunwayworld.emc.module.item.managereview.resource;

import org.springframework.web.bind.annotation.RequestMapping;

import com.sunwayworld.emc.module.item.managereview.bean.EmcManageReviewBean;
import com.sunwayworld.emc.module.item.managereview.service.EmcManageReviewService;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.support.auditable.resource.GenericAuditableResource;
import com.sunwayworld.framework.support.base.resource.GenericResource;

@RequestMapping("/secure/emc/module/item/manage-reviews")
public interface EmcManageReviewResource extends GenericResource<EmcManageReviewService, EmcManageReviewBean, Long>, GenericAuditableResource<EmcManageReviewService, EmcManageReviewBean, Long> {

    Long insert(RestJsonWrapperBean wrapper);

}
