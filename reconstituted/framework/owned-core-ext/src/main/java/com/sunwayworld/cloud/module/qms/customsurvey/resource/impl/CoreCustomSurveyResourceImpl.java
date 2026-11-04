/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.qms.customsurvey.resource.impl;

import com.sunwayworld.cloud.module.qms.customsurvey.bean.CoreCustomSurveyBean;
import com.sunwayworld.cloud.module.qms.customsurvey.bean.CoreCustomSurveyItemBean;
import com.sunwayworld.cloud.module.qms.customsurvey.bean.CoreCustomSurveySumBean;
import com.sunwayworld.cloud.module.qms.customsurvey.bean.CoreCustomSurveySumRetBean;
import com.sunwayworld.cloud.module.qms.customsurvey.resource.CoreCustomSurveyResource;
import com.sunwayworld.cloud.module.qms.customsurvey.service.CoreCustomSurveyService;
import com.sunwayworld.cloud.module.qms.customsurvey.validator.CoreCustomSurveyCompleteValidator;
import com.sunwayworld.framework.data.page.Page;
import com.sunwayworld.framework.log.annotation.Log;
import com.sunwayworld.framework.log.annotation.LogModule;
import com.sunwayworld.framework.log.annotation.LogType;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import com.sunwayworld.framework.support.auditable.resource.GenericAuditableResource;
import com.sunwayworld.framework.support.base.bean.CoreServiceStatusDTO;
import com.sunwayworld.framework.support.base.resource.AbstractGenericResource;
import com.sunwayworld.framework.validator.data.annotation.ValidateDataWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@LogModule(value="\u8c03\u67e5\u4fe1\u606f")
@RestController
@GikamBean
public class CoreCustomSurveyResourceImpl
implements CoreCustomSurveyResource,
GenericAuditableResource<CoreCustomSurveyService, CoreCustomSurveyBean, Long>,
AbstractGenericResource<CoreCustomSurveyService, CoreCustomSurveyBean, Long> {
    @Autowired
    private CoreCustomSurveyService coreCustomSurveyService;

    @Override
    public CoreCustomSurveyService getService() {
        return this.coreCustomSurveyService;
    }

    @Override
    @Log(value="\u65b0\u589e\u8c03\u67e5\u4fe1\u606f", type=LogType.INSERT)
    @RequestMapping(method={RequestMethod.POST})
    public Long insert(RestJsonWrapperBean wrapper) {
        return (Long)this.getService().insert(wrapper);
    }

    @Override
    @Log(value="\u6839\u636e\u8c03\u67e5\u4fe1\u606fID\u67e5\u8be2\u8c03\u67e5\u4fe1\u606f-\u5185\u5bb9", type=LogType.SELECT)
    @RequestMapping(value={"/{id}/survey-items/queries"}, method={RequestMethod.POST})
    public Page<CoreCustomSurveyItemBean> selectSurveyItemPaginationByCustomSurveyId(@PathVariable Long id, RestJsonWrapperBean wrapper) {
        return this.getService().selectSurveyItemPaginationByCustomSurveyId(id, wrapper);
    }

    @Override
    @Log(value="\u67e5\u8be2\u8c03\u67e5\u4fe1\u606f\u7edf\u8ba1\u5217\u8868", type=LogType.SELECT)
    @RequestMapping(value={"/survey-sums/queries"}, method={RequestMethod.POST})
    public Page<CoreCustomSurveySumBean> selectSurveySumPagination(RestJsonWrapperBean wrapper) {
        return this.getService().selectSurveySumPagination(wrapper);
    }

    @Override
    @Log(value="\u67e5\u8be2\u8c03\u67e5\u4fe1\u606f\u7edf\u8ba1\u5217\u8868", type=LogType.SELECT)
    @RequestMapping(value={"/survey-sums/tasks"}, method={RequestMethod.POST})
    public Page<CoreCustomSurveySumBean> selectSurveySumAuditablePagination(RestJsonWrapperBean wrapper) {
        return this.getService().selectSurveySumAuditablePagination(wrapper);
    }

    @Override
    @Log(value="\u8c03\u67e5\u4fe1\u606f\u8be6\u7ec6\u4fe1\u606f\u67e5\u8be2", type=LogType.SELECT)
    @RequestMapping(value={"/{id}/survey-sums/queries"}, method={RequestMethod.GET})
    public CoreCustomSurveySumBean selectCustomSurveySumDetail(@PathVariable Long id) {
        return this.getService().selectCustomSurveySumDetail(id);
    }

    @Override
    @Log(value="\u65b0\u589e\u8c03\u67e5\u4fe1\u606f\u7edf\u8ba1", type=LogType.INSERT)
    @RequestMapping(value={"/survey-sums"}, method={RequestMethod.POST})
    public Long insertSurveySum(RestJsonWrapperBean wrapper) {
        return this.getService().insertSurveySum(wrapper);
    }

    @Override
    @Log(value="\u5220\u9664\u8c03\u67e5\u4fe1\u606f\u7edf\u8ba1", type=LogType.DELETE)
    @RequestMapping(value={"/survey-sums"}, method={RequestMethod.DELETE})
    public void deleteSurveySum(RestJsonWrapperBean wrapper) {
        this.getService().deleteSurveySum(wrapper);
    }

    @Override
    @Log(value="\u6839\u636e\u8c03\u67e5\u4fe1\u606fID\u67e5\u8be2\u8c03\u67e5\u4fe1\u606f\u7edf\u8ba1\u7ed3\u679c", type=LogType.SELECT)
    @RequestMapping(value={"/{id}/survey-sum-rets/queries"}, method={RequestMethod.POST})
    public Page<CoreCustomSurveySumRetBean> selectSurveySumRetPaginationByCustomSurveyId(@PathVariable Long id, RestJsonWrapperBean wrapper) {
        return this.getService().selectSurveySumRetPaginationByCustomSurveyId(id, wrapper);
    }

    @Override
    @Log(value="\u5b8c\u6210\u8c03\u67e5\u4fe1\u606f", type=LogType.UPDATE)
    @RequestMapping(value={"/action/complete"}, method={RequestMethod.PUT})
    @ValidateDataWith(value=CoreCustomSurveyCompleteValidator.class)
    public void completeCustomSurvey(RestJsonWrapperBean wrapper) {
        this.getService().completeCustomSurvey(wrapper);
    }

    @Override
    @Log(value="\u6839\u636eid\u67e5\u8be2\u5de5\u4f5c\u6d41\u72b6\u6001\u4fe1\u606f", type=LogType.SELECT)
    @RequestMapping(value={"/{id}/info/status"}, method={RequestMethod.GET})
    public CoreServiceStatusDTO selectStatus(@PathVariable(required=false) Long id, @RequestParam(required=false) String currentStatusCode) {
        return this.getService().getBpmnInstanceTaskById(id);
    }
}
