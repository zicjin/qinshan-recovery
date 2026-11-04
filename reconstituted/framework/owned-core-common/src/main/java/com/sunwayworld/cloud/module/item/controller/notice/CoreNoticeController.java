/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.item.controller.notice;

import com.sunwayworld.framework.controller.GenericAuditListPageController;
import com.sunwayworld.framework.controller.GenericDetailPageController;
import com.sunwayworld.framework.controller.GenericEditListPageController;
import com.sunwayworld.framework.controller.GenericSearchListPageController;
import org.springframework.web.bind.annotation.RequestMapping;

@RequestMapping(value={"/secure/core/module/item/notices/page"})
public interface CoreNoticeController
extends GenericEditListPageController,
GenericAuditListPageController,
GenericSearchListPageController,
GenericDetailPageController {
    @RequestMapping(value={"/cancel-list"})
    public String cancelListPage();
}
