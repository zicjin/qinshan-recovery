/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.train.exampaper.resource;

import com.sunwayworld.cloud.module.train.exampaper.bean.CoreExamPaperAnswerBean;
import com.sunwayworld.cloud.module.train.exampaper.bean.CoreExamPaperAnswerCardDTO;
import com.sunwayworld.cloud.module.train.exampaper.bean.CoreExamPaperBean;
import com.sunwayworld.cloud.module.train.question.bean.CoreQuestionOptBean;
import com.sunwayworld.framework.data.page.Pagination;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.support.activatable.resource.GenericActivatableCloudResource;
import com.sunwayworld.framework.support.base.resource.GenericCloudResource;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@RequestMapping(value={"/secure/core/module/train/exam-papers"})
public interface CoreExamPaperResource
extends GenericCloudResource<CoreExamPaperBean, Long>,
GenericActivatableCloudResource<CoreExamPaperBean, Long> {
    @RequestMapping(value={"/action/start-exam"}, method={RequestMethod.PUT})
    public CoreExamPaperBean startExam(RestJsonWrapperBean var1);

    @RequestMapping(value={"/action/end-exam"}, method={RequestMethod.PUT})
    public void endExam(RestJsonWrapperBean var1);

    @RequestMapping(value={"/{id}/answer-cards/queries"}, method={RequestMethod.POST})
    public Pagination<CoreExamPaperAnswerCardDTO> selectExamPaperAnswerCardByExamPaperId(@PathVariable Long var1);

    @RequestMapping(value={"/{id}/answers/queries"}, method={RequestMethod.POST})
    public Pagination<CoreExamPaperAnswerBean> selectExamPaperAnswerPaginationByExamPaperId(@PathVariable Long var1, RestJsonWrapperBean var2);

    @RequestMapping(value={"/{id}/answers/{orderNo}"}, method={RequestMethod.GET})
    public CoreExamPaperAnswerBean selectExamPaperAnswerByExamPaperIdAndOrderNo(@PathVariable Long var1, @PathVariable Integer var2);

    @RequestMapping(value={"/answers/{id}"}, method={RequestMethod.GET})
    public CoreExamPaperAnswerBean selectExamPaperAnswerById(@PathVariable Long var1);

    @RequestMapping(value={"/answers/{answerId}/question-opts/queries"}, method={RequestMethod.POST})
    public Pagination<CoreQuestionOptBean> selectQuestionOptPaginationByAnswerId(@PathVariable Long var1, RestJsonWrapperBean var2);
}
