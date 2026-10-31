package com.sunwayworld.emc.module.item.qualitysupervision.resource;

import org.springframework.web.bind.annotation.RequestMapping;

import com.sunwayworld.emc.module.item.qualitysupervision.bean.EmcQualitySupervisionBean;
import com.sunwayworld.emc.module.item.qualitysupervision.bean.EmcQualitySupervisionContBean;
import com.sunwayworld.emc.module.item.qualitysupervision.bean.EmcQualitySupervisionPlanBean;
import com.sunwayworld.emc.module.item.qualitysupervision.service.EmcQualitySupervisionService;
import com.sunwayworld.framework.data.page.Page;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.support.auditable.resource.GenericAuditableResource;
import com.sunwayworld.framework.support.base.resource.GenericResource;

/**
 * @author zhangft@sunwayworld.com
 * @date 2019-12-10
 */
@RequestMapping("/secure/emc/module/item/quality-supervisions")
public interface EmcQualitySupervisionResource extends GenericResource<EmcQualitySupervisionService, EmcQualitySupervisionBean, Long>,
                                                       GenericAuditableResource<EmcQualitySupervisionService, EmcQualitySupervisionBean, Long> {

    Page<EmcQualitySupervisionPlanBean> selectQualitySupervisionPlanPaginationBySupervisionId(Long supervisionId, RestJsonWrapperBean wrapper);

    Long insertQualitySupervisionPlan(Long supervisionId, RestJsonWrapperBean wrapper);

    void deleteQualitySupervisionPlan(RestJsonWrapperBean wrapper);

    Page<EmcQualitySupervisionContBean> selectQualitySupervisionContPaginationByPlanId(Long planId, RestJsonWrapperBean wrapper);

    Long insertQualitySupervisionCont(Long planId, RestJsonWrapperBean wrapper);

    void deleteQualitySupervisionCont(RestJsonWrapperBean wrapper);
}
