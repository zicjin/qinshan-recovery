/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.qms.customsurvey.service;

import com.sunwayworld.cloud.module.qms.customsurvey.bean.CoreCustomSurveyBean;
import com.sunwayworld.cloud.module.qms.customsurvey.bean.CoreCustomSurveyItemBean;
import com.sunwayworld.cloud.module.qms.customsurvey.bean.CoreCustomSurveySumBean;
import com.sunwayworld.cloud.module.qms.customsurvey.bean.CoreCustomSurveySumRetBean;
import com.sunwayworld.framework.data.page.Page;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.support.auditable.service.GenericAuditableService;
import com.sunwayworld.framework.support.base.bean.CoreServiceStatusDTO;
import com.sunwayworld.framework.support.base.service.GenericService;

public interface CoreCustomSurveyService
extends GenericService<CoreCustomSurveyBean, Long>,
GenericAuditableService<CoreCustomSurveyBean, Long> {
    public Page<CoreCustomSurveyItemBean> selectSurveyItemPaginationByCustomSurveyId(Long var1, RestJsonWrapperBean var2);

    public Page<CoreCustomSurveySumBean> selectSurveySumPagination(RestJsonWrapperBean var1);

    public Long insertSurveySum(RestJsonWrapperBean var1);

    public void deleteSurveySum(RestJsonWrapperBean var1);

    public Page<CoreCustomSurveySumRetBean> selectSurveySumRetPaginationByCustomSurveyId(Long var1, RestJsonWrapperBean var2);

    public void completeCustomSurvey(RestJsonWrapperBean var1);

    public CoreCustomSurveySumBean selectCustomSurveySumDetail(Long var1);

    public CoreServiceStatusDTO getBpmnInstanceTaskById(Long var1);

    public Page<CoreCustomSurveySumBean> selectSurveySumAuditablePagination(RestJsonWrapperBean var1);
}
