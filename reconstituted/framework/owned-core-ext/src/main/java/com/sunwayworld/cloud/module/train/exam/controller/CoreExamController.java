/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.train.exam.controller;

import com.sunwayworld.framework.controller.GenericChoosePageController;
import com.sunwayworld.framework.controller.GenericDetailPageController;
import com.sunwayworld.framework.controller.GenericEditListPageController;
import org.springframework.web.bind.annotation.RequestMapping;

@RequestMapping(value={"/secure/core/module/train/exams/page"})
public interface CoreExamController
extends GenericEditListPageController,
GenericChoosePageController,
GenericDetailPageController {
    @RequestMapping(value={"/question-edit"})
    public String questionEditPage();
}
