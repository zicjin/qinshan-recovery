package com.sunwayworld.emc.module.item.qualitysupervision.service;

import com.sunwayworld.emc.module.item.qualitysupervision.bean.EmcQualitySupervisionBean;
import com.sunwayworld.emc.module.item.qualitysupervision.bean.EmcQualitySupervisionContBean;
import com.sunwayworld.emc.module.item.qualitysupervision.bean.EmcQualitySupervisionPlanBean;
import com.sunwayworld.framework.data.page.Page;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.support.auditable.service.GenericAuditableService;
import com.sunwayworld.framework.support.base.service.GenericService;


/**
 * @author zhangft@sunwayworld.com
 * @date 2019-12-10
 */
public interface EmcQualitySupervisionService extends GenericService<EmcQualitySupervisionBean, Long>,
                                                      GenericAuditableService<EmcQualitySupervisionBean, Long> {

    /**
     * 根据质量监督查询质量监督计划
     * 
     * @param supervisionId
     * @param wrapper
     * @return
     */
    Page<EmcQualitySupervisionPlanBean> selectQualitySupervisionPlanPaginationBySupervisionId(Long supervisionId, RestJsonWrapperBean wrapper);

    /**
     * 新增质量监督计划
     * 
     * @param supervisionId
     * @param wrapper
     * @return
     */
    Long insertQualitySupervisionPlan(Long supervisionId, RestJsonWrapperBean wrapper);

    /**
     * 删除质量监督计划
     * 
     * @param wrapper
     */
    void deleteQualitySupervisionPlan(RestJsonWrapperBean wrapper);

    /**
     * 根据质量监督计划查询质量监督内容
     * 
     * @param planId
     * @param wrapper
     * @return
     */
    Page<EmcQualitySupervisionContBean> selectQualitySupervisionContPaginationByPlanId(Long planId, RestJsonWrapperBean wrapper);

    /**
     * 新增质量监督内容
     * 
     * @param planId
     * @param wrapper
     * @return
     */
    Long insertQualitySupervisionCont(Long planId, RestJsonWrapperBean wrapper);

    /**
     * 删除质量监督内容
     * 
     * @param wrapper
     */
    void deleteQualitySupervisionCont(RestJsonWrapperBean wrapper);
}
