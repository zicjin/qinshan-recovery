/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.qms.customsrtsurvey.resource;

import com.sunwayworld.cloud.module.qms.customsrtsurvey.bean.CoreCustomSrtSurveyBean;
import com.sunwayworld.cloud.module.qms.customsrtsurvey.bean.CoreCustomSrtSurveyItemBean;
import com.sunwayworld.framework.data.page.Page;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.support.base.resource.GenericCloudResource;
import org.springframework.web.bind.annotation.RequestMapping;

@RequestMapping(value={"/secure/core/module/qms/custom-srt-surveys"})
public interface CoreCustomSrtSurveyResource
extends GenericCloudResource<CoreCustomSrtSurveyBean, Long> {
    public Page<CoreCustomSrtSurveyItemBean> selectSrtSurveyItemPaginationByCustomSrtSurveyId(Long var1, RestJsonWrapperBean var2);

    public Long insertSrtSurveyItem(Long var1, RestJsonWrapperBean var2);

    public void deleteSrtSurveyItem(Long var1, RestJsonWrapperBean var2);
}
