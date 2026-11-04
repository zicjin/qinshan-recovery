/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.train.exampaper.service;

import com.sunwayworld.cloud.module.train.exampaper.bean.CoreExamPaperAnswerBean;
import com.sunwayworld.cloud.module.train.exampaper.bean.CoreExamPaperAnswerCardDTO;
import com.sunwayworld.cloud.module.train.question.bean.CoreQuestionOptBean;
import com.sunwayworld.framework.data.page.Pagination;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.support.base.service.GenericService;

public interface CoreExamPaperAnswerService
extends GenericService<CoreExamPaperAnswerBean, Long> {
    public Pagination<CoreExamPaperAnswerCardDTO> selectExamPaperAnswerCardByExamPaperId(Long var1);

    public CoreExamPaperAnswerBean selectExamPaperAnswerCardByExamPaperId(Long var1, Integer var2);

    public CoreExamPaperAnswerBean selectExamPaperAnswerById(Long var1);

    public Pagination<CoreQuestionOptBean> selectQuestionOptPaginationByAnswerId(Long var1, RestJsonWrapperBean var2);
}
