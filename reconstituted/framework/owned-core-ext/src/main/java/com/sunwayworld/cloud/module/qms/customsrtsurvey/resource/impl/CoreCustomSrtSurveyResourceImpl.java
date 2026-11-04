/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.qms.customsrtsurvey.resource.impl;

import com.sunwayworld.cloud.module.qms.customsrtsurvey.bean.CoreCustomSrtSurveyBean;
import com.sunwayworld.cloud.module.qms.customsrtsurvey.bean.CoreCustomSrtSurveyItemBean;
import com.sunwayworld.cloud.module.qms.customsrtsurvey.resource.CoreCustomSrtSurveyResource;
import com.sunwayworld.cloud.module.qms.customsrtsurvey.service.CoreCustomSrtSurveyService;
import com.sunwayworld.cloud.module.qms.customsrtsurvey.validator.CoreCustomSrtSurveyItemValidator;
import com.sunwayworld.framework.data.page.Page;
import com.sunwayworld.framework.log.annotation.Log;
import com.sunwayworld.framework.log.annotation.LogModule;
import com.sunwayworld.framework.log.annotation.LogType;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import com.sunwayworld.framework.support.auditable.resource.GenericAuditableResource;
import com.sunwayworld.framework.support.base.resource.AbstractGenericResource;
import com.sunwayworld.framework.support.choosable.resource.GenericChoosableResource;
import com.sunwayworld.framework.validator.data.annotation.ValidateDataWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@LogModule(value="\u4fdd\u62a4\u5ba2\u6237\u673a\u5bc6\u8c03\u67e5\u8868")
@RestController
@GikamBean
public class CoreCustomSrtSurveyResourceImpl
implements CoreCustomSrtSurveyResource,
AbstractGenericResource<CoreCustomSrtSurveyService, CoreCustomSrtSurveyBean, Long>,
GenericAuditableResource<CoreCustomSrtSurveyService, CoreCustomSrtSurveyBean, Long>,
GenericChoosableResource<CoreCustomSrtSurveyService, CoreCustomSrtSurveyBean, Long> {
    @Autowired
    private CoreCustomSrtSurveyService coreCustomSrtSurveyService;

    @Override
    public CoreCustomSrtSurveyService getService() {
        return this.coreCustomSrtSurveyService;
    }

    @Override
    @Log(value="\u65b0\u589e\u4fdd\u62a4\u5ba2\u6237\u673a\u5bc6\u8c03\u67e5\u8868", type=LogType.INSERT)
    @RequestMapping(method={RequestMethod.POST})
    public Long insert(RestJsonWrapperBean wrapper) {
        return (Long)this.getService().insert(wrapper);
    }

    @Override
    @Log(value="\u6839\u636e\u4fdd\u62a4\u5ba2\u6237\u673a\u5bc6\u8c03\u67e5\u8868ID\u67e5\u8be2\u4fdd\u62a4\u5ba2\u6237\u673a\u5bc6\u8c03\u67e5\u8868\u5185\u5bb9", type=LogType.SELECT)
    @RequestMapping(value={"/{id}/srt-survey-items/queries"}, method={RequestMethod.POST})
    public Page<CoreCustomSrtSurveyItemBean> selectSrtSurveyItemPaginationByCustomSrtSurveyId(@PathVariable Long id, RestJsonWrapperBean wrapper) {
        return this.getService().selectSrtSurveyItemPaginationByCustomSrtSurveyId(id, wrapper);
    }

    @Override
    @Log(value="\u65b0\u589e\u4fdd\u62a4\u5ba2\u6237\u673a\u5bc6\u8c03\u67e5\u8868\u5185\u5bb9", type=LogType.INSERT)
    @RequestMapping(value={"/{id}/srt-survey-items"}, method={RequestMethod.POST})
    @ValidateDataWith(value=CoreCustomSrtSurveyItemValidator.class)
    public Long insertSrtSurveyItem(@PathVariable Long id, RestJsonWrapperBean wrapper) {
        return this.getService().insertSrtSurveyItem(id, wrapper);
    }

    @Override
    @Log(value="\u5220\u9664\u4fdd\u62a4\u5ba2\u6237\u673a\u5bc6\u8c03\u67e5\u8868\u5185\u5bb9", type=LogType.DELETE)
    @RequestMapping(value={"/{id}/srt-survey-items"}, method={RequestMethod.DELETE})
    public void deleteSrtSurveyItem(@PathVariable Long id, RestJsonWrapperBean wrapper) {
        this.getService().deleteSrtSurveyItem(id, wrapper);
    }
}
