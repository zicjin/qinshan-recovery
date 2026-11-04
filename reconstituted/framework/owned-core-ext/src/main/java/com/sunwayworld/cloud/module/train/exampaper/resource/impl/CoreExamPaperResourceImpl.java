/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.train.exampaper.resource.impl;

import com.sunwayworld.cloud.module.train.exampaper.bean.CoreExamPaperAnswerBean;
import com.sunwayworld.cloud.module.train.exampaper.bean.CoreExamPaperAnswerCardDTO;
import com.sunwayworld.cloud.module.train.exampaper.bean.CoreExamPaperBean;
import com.sunwayworld.cloud.module.train.exampaper.resource.CoreExamPaperResource;
import com.sunwayworld.cloud.module.train.exampaper.service.CoreExamPaperAnswerService;
import com.sunwayworld.cloud.module.train.exampaper.service.CoreExamPaperService;
import com.sunwayworld.cloud.module.train.question.bean.CoreQuestionOptBean;
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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

@LogModule(value="\u8003\u5377")
@RestController
@GikamBean
public class CoreExamPaperResourceImpl
implements CoreExamPaperResource,
AbstractGenericResource<CoreExamPaperService, CoreExamPaperBean, Long>,
AbstractGenericActivatableResource<CoreExamPaperService, CoreExamPaperBean, Long> {
    @Autowired
    private CoreExamPaperService coreExamPaperService;
    @Autowired
    private CoreExamPaperAnswerService examPaperAnswerService;

    @Override
    public CoreExamPaperService getService() {
        return this.coreExamPaperService;
    }

    @Override
    @Log(value="\u5f00\u59cb\u8003\u8bd5", type=LogType.UPDATE)
    public CoreExamPaperBean startExam(RestJsonWrapperBean wrapper) {
        return this.coreExamPaperService.startExam(wrapper);
    }

    @Override
    @Log(value="\u7ed3\u675f\u8003\u8bd5", type=LogType.UPDATE)
    public void endExam(RestJsonWrapperBean wrapper) {
        this.coreExamPaperService.endExam(wrapper);
    }

    @Override
    @Log(value="\u7b54\u9898\u5361\u67e5\u8be2", type=LogType.SELECT)
    public Pagination<CoreExamPaperAnswerCardDTO> selectExamPaperAnswerCardByExamPaperId(Long id) {
        return this.examPaperAnswerService.selectExamPaperAnswerCardByExamPaperId(id);
    }

    @Override
    @Log(value="\u6839\u636e\u8003\u5377ID\u548c\u6392\u5e8f\u53f7\u67e5\u8be2\u8003\u5377\u7b54\u6848", type=LogType.SELECT)
    public CoreExamPaperAnswerBean selectExamPaperAnswerByExamPaperIdAndOrderNo(Long id, Integer orderNo) {
        return this.examPaperAnswerService.selectExamPaperAnswerCardByExamPaperId(id, orderNo);
    }

    @Override
    @Log(value="\u6839\u636eID\u67e5\u8be2\u8003\u5377\u7b54\u6848", type=LogType.SELECT)
    public CoreExamPaperAnswerBean selectExamPaperAnswerById(Long id) {
        return this.examPaperAnswerService.selectExamPaperAnswerById(id);
    }

    @Override
    @Log(value="\u6839\u636e\u8003\u5377\u7b54\u6848ID\u67e5\u8be2\u8bd5\u9898\u9009\u9879", type=LogType.SELECT)
    public Pagination<CoreQuestionOptBean> selectQuestionOptPaginationByAnswerId(Long answerId, RestJsonWrapperBean wrapper) {
        return this.examPaperAnswerService.selectQuestionOptPaginationByAnswerId(answerId, wrapper);
    }

    @Override
    @Log(value="\u6839\u636e\u8003\u5377ID\u67e5\u8be2\u7b54\u6848", type=LogType.SELECT)
    public Pagination<CoreExamPaperAnswerBean> selectExamPaperAnswerPaginationByExamPaperId(Long id, RestJsonWrapperBean wrapper) {
        return (Pagination)this.examPaperAnswerService.selectPaginationByFilter(SearchFilter.instance().match("EXAMPAPERID", id).filter(MatchPattern.EQ), wrapper);
    }
}
