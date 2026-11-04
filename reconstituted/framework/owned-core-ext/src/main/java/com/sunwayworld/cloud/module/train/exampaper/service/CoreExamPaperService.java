/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.train.exampaper.service;

import com.sunwayworld.cloud.module.train.exampaper.bean.CoreExamPaperBean;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.support.activatable.service.GenericActivatableService;
import com.sunwayworld.framework.support.base.service.GenericService;

public interface CoreExamPaperService
extends GenericService<CoreExamPaperBean, Long>,
GenericActivatableService<CoreExamPaperBean, Long> {
    public CoreExamPaperBean startExam(RestJsonWrapperBean var1);

    public void endExam(RestJsonWrapperBean var1);

    public void generateExamPaper(Long var1, Long var2, String var3, String var4);
}
