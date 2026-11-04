/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.item.controller.pvconf;

import com.sunwayworld.framework.controller.GenericEditListPageController;
import org.springframework.web.bind.annotation.RequestMapping;

@RequestMapping(value={"/secure/core/module/item/process-view-confs/page"})
public interface CoreProcessViewConfController
extends GenericEditListPageController {
    @RequestMapping(value={"/grid-conf"})
    public String gridConfPage();

    @RequestMapping(value={"/edit-sql"})
    public String editSqlPage();
}
