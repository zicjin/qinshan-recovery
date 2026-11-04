/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.codegenerator.sqlgeneratetable.controller;

import com.sunwayworld.framework.controller.GenericEditListPageController;
import org.springframework.web.bind.annotation.RequestMapping;

@RequestMapping(value={"/secure/core/module/codegenerator/sql-generate-tables/page"})
public interface CoreSqlGenerateTableController
extends GenericEditListPageController {
    public String sqlPage();
}
