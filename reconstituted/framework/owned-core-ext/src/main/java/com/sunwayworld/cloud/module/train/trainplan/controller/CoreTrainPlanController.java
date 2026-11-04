/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.train.trainplan.controller;

import com.sunwayworld.framework.controller.GenericAuditListPageController;
import com.sunwayworld.framework.controller.GenericChoosePageController;
import com.sunwayworld.framework.controller.GenericDetailPageController;
import com.sunwayworld.framework.controller.GenericEditListPageController;
import org.springframework.web.bind.annotation.RequestMapping;

@RequestMapping(value={"/secure/core/module/train/train-plans/page"})
public interface CoreTrainPlanController
extends GenericEditListPageController,
GenericAuditListPageController,
GenericDetailPageController,
GenericChoosePageController {
    @RequestMapping(value={"/user-search"})
    public String userSearchPage();
}
