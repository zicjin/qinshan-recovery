/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.qms.customsrtsurvey.service;

import com.sunwayworld.cloud.module.qms.customsrtsurvey.bean.CoreCustomSrtSurveyBean;
import com.sunwayworld.cloud.module.qms.customsrtsurvey.bean.CoreCustomSrtSurveyItemBean;
import com.sunwayworld.framework.data.page.Page;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.support.auditable.service.GenericAuditableService;
import com.sunwayworld.framework.support.base.service.GenericService;
import com.sunwayworld.framework.support.choosable.service.GenericChoosableService;

public interface CoreCustomSrtSurveyService
extends GenericService<CoreCustomSrtSurveyBean, Long>,
GenericAuditableService<CoreCustomSrtSurveyBean, Long>,
GenericChoosableService<CoreCustomSrtSurveyBean, Long> {
    public Page<CoreCustomSrtSurveyItemBean> selectSrtSurveyItemPaginationByCustomSrtSurveyId(Long var1, RestJsonWrapperBean var2);

    public Long insertSrtSurveyItem(Long var1, RestJsonWrapperBean var2);

    public void deleteSrtSurveyItem(Long var1, RestJsonWrapperBean var2);
}
