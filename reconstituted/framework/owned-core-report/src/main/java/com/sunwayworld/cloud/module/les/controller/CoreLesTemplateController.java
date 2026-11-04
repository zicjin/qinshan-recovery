/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.les.controller;

import com.sunwayworld.framework.controller.GenericAuditListPageController;
import com.sunwayworld.framework.controller.GenericChoosePageController;
import com.sunwayworld.framework.controller.GenericDetailPageController;
import com.sunwayworld.framework.controller.GenericEditListPageController;
import org.springframework.web.bind.annotation.RequestMapping;

@RequestMapping(value={"/secure/core/les/les-templates/page"})
public interface CoreLesTemplateController
extends GenericEditListPageController,
GenericDetailPageController,
GenericAuditListPageController,
GenericChoosePageController {
    @RequestMapping(value={"/spread-sheet"})
    public String spreadSheetPage();

    @RequestMapping(value={"/chem-doodle"})
    public String chemDoodlePage();
}
