/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.qms.riskidentificaregist.controller;

import com.sunwayworld.framework.controller.GenericAuditListPageController;
import com.sunwayworld.framework.controller.GenericChooseMultPageController;
import com.sunwayworld.framework.controller.GenericChoosePageController;
import com.sunwayworld.framework.controller.GenericDetailPageController;
import com.sunwayworld.framework.controller.GenericEditListPageController;
import org.springframework.web.bind.annotation.RequestMapping;

@RequestMapping(value={"/secure/core/module/qms/risk-identifica-regists/page"})
public interface CoreRiskIdentificaRegistController
extends GenericEditListPageController,
GenericDetailPageController,
GenericAuditListPageController,
GenericChoosePageController,
GenericChooseMultPageController {
    @RequestMapping(value={"/assess-edit-list"})
    public String assessEditListPage();

    @RequestMapping(value={"/assess-audit-list"})
    public String assessAuditListPage();

    @RequestMapping(value={"/execute-edit-list"})
    public String executeEditListPage();

    @RequestMapping(value={"/execute-audit-list"})
    public String executeAuditListPage();

    @RequestMapping(value={"/archive-search-list"})
    public String archiveSearchListPage();
}
