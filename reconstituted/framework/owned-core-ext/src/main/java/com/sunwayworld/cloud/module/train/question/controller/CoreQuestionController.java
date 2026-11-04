/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.train.question.controller;

import com.sunwayworld.framework.controller.GenericDetailPageController;
import com.sunwayworld.framework.controller.GenericEditListPageController;
import org.springframework.web.bind.annotation.RequestMapping;

@RequestMapping(value={"/secure/core/module/train/questions/page"})
public interface CoreQuestionController
extends GenericEditListPageController,
GenericDetailPageController {
    @RequestMapping(value={"/question-bank-detail"})
    public String questionBankDetailPage();
}
