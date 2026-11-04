/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.train.exam.resource;

import com.sunwayworld.cloud.module.train.exam.bean.CoreExamBean;
import com.sunwayworld.cloud.module.train.exam.bean.CoreExamQuestionBean;
import com.sunwayworld.framework.data.page.Pagination;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.support.activatable.resource.GenericActivatableCloudResource;
import com.sunwayworld.framework.support.base.resource.GenericCloudResource;
import com.sunwayworld.framework.support.choosable.resource.GenericChoosableCloudResource;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@RequestMapping(value={"/secure/core/module/train/exams"})
public interface CoreExamResource
extends GenericCloudResource<CoreExamBean, Long>,
GenericActivatableCloudResource<CoreExamBean, Long>,
GenericChoosableCloudResource<CoreExamBean, Long> {
    @RequestMapping(value={"/questions"}, method={RequestMethod.POST})
    public void insertQuestion(RestJsonWrapperBean var1);

    @RequestMapping(value={"/questions"}, method={RequestMethod.DELETE})
    public void deleteQuestion(RestJsonWrapperBean var1);

    @RequestMapping(value={"/{id}/questions/queries"}, method={RequestMethod.POST})
    public Pagination<CoreExamQuestionBean> selectExamQuestionPaginationByExamId(@PathVariable Long var1, RestJsonWrapperBean var2);
}
