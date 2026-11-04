/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.qms.ncr.controller;

import com.sunwayworld.framework.controller.GenericAuditListPageController;
import com.sunwayworld.framework.controller.GenericChoosePageController;
import com.sunwayworld.framework.controller.GenericDetailPageController;
import com.sunwayworld.framework.controller.GenericEditListPageController;
import com.sunwayworld.framework.controller.GenericSearchListPageController;
import org.springframework.web.bind.annotation.RequestMapping;

@RequestMapping(value={"/secure/core/module/qms/ncrs/page"})
public interface CoreNcrController
extends GenericEditListPageController,
GenericDetailPageController,
GenericAuditListPageController,
GenericSearchListPageController,
GenericChoosePageController {
}
