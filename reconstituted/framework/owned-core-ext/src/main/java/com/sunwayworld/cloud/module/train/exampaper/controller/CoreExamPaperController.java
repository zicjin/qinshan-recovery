/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.train.exampaper.controller;

import com.sunwayworld.framework.controller.GenericDetailPageController;
import com.sunwayworld.framework.controller.GenericEditListPageController;
import org.springframework.web.bind.annotation.RequestMapping;

@RequestMapping(value={"/secure/core/module/train/exam-papers/page"})
public interface CoreExamPaperController
extends GenericEditListPageController,
GenericDetailPageController {
    @RequestMapping(value={"/review-list"})
    public String reviewListPage();

    @RequestMapping(value={"/review-detail"})
    public String reviewDetailPage();
}
