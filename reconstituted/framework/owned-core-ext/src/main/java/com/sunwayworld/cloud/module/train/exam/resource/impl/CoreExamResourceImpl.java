/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.train.exam.resource.impl;

import com.sunwayworld.cloud.module.train.exam.bean.CoreExamBean;
import com.sunwayworld.cloud.module.train.exam.bean.CoreExamQuestionBean;
import com.sunwayworld.cloud.module.train.exam.resource.CoreExamResource;
import com.sunwayworld.cloud.module.train.exam.service.CoreExamQuestionService;
import com.sunwayworld.cloud.module.train.exam.service.CoreExamService;
import com.sunwayworld.cloud.module.train.exam.validator.CoreExamActivatableValidator;
import com.sunwayworld.cloud.module.train.exam.validator.CoreExamDeleteableValidator;
import com.sunwayworld.framework.data.page.Pagination;
import com.sunwayworld.framework.log.annotation.Log;
import com.sunwayworld.framework.log.annotation.LogModule;
import com.sunwayworld.framework.log.annotation.LogType;
import com.sunwayworld.framework.mybatis.mapper.MatchPattern;
import com.sunwayworld.framework.mybatis.mapper.SearchFilter;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import com.sunwayworld.framework.support.activatable.resource.AbstractGenericActivatableResource;
import com.sunwayworld.framework.support.base.resource.AbstractGenericResource;
import com.sunwayworld.framework.support.choosable.resource.AbstractGenericChoosableResource;
import com.sunwayworld.framework.validator.data.annotation.ValidateDataWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

@LogModule(value="\u8bd5\u5377")
@RestController
@GikamBean
public class CoreExamResourceImpl
implements CoreExamResource,
AbstractGenericResource<CoreExamService, CoreExamBean, Long>,
AbstractGenericActivatableResource<CoreExamService, CoreExamBean, Long>,
AbstractGenericChoosableResource<CoreExamService, CoreExamBean, Long> {
    @Autowired
    private CoreExamService coreExamService;
    @Autowired
    private CoreExamQuestionService coreExamQuestionService;

    @Override
    public CoreExamService getService() {
        return this.coreExamService;
    }

    @Override
    @Log(value="\u5220\u9664\u8bd5\u5377", type=LogType.DELETE)
    @ValidateDataWith(value=CoreExamDeleteableValidator.class)
    public void delete(RestJsonWrapperBean wrapper) {
        this.coreExamService.delete(wrapper);
    }

    @Override
    @Log(value="\u65b0\u589e\u8bd5\u5377\u9898\u76ee", type=LogType.INSERT)
    public void insertQuestion(RestJsonWrapperBean wrapper) {
        this.coreExamQuestionService.insert(wrapper);
    }

    @Override
    @Log(value="\u5220\u9664\u8bd5\u5377\u9898\u76ee", type=LogType.DELETE)
    public void deleteQuestion(RestJsonWrapperBean wrapper) {
        this.coreExamQuestionService.delete(wrapper);
    }

    @Override
    @Log(value="\u6839\u636e\u8bd5\u5377\u67e5\u8be2\u8bd5\u5377\u9898\u76ee", type=LogType.SELECT)
    public Pagination<CoreExamQuestionBean> selectExamQuestionPaginationByExamId(Long id, RestJsonWrapperBean wrapper) {
        return (Pagination)this.coreExamQuestionService.selectPaginationByFilter(SearchFilter.instance().match("EXAMID", id).filter(MatchPattern.EQ), wrapper);
    }

    @Override
    @Log(value="\u9501\u5b9a(\u53d1\u5e03)\u8bd5\u5377", type=LogType.ACTIVATE)
    @ValidateDataWith(value=CoreExamActivatableValidator.class)
    public void activate(RestJsonWrapperBean wrapper) {
        this.getService().activate(wrapper);
    }
}
