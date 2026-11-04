/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.email.controller;

import com.sunwayworld.framework.controller.GenericDetailPageController;
import com.sunwayworld.framework.controller.GenericEditListPageController;
import org.springframework.web.bind.annotation.RequestMapping;

@RequestMapping(value={"/secure/core/module/item/email-configs/page"})
public interface CoreEmailConfigController
extends GenericEditListPageController,
GenericDetailPageController {
}
