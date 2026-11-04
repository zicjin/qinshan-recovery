/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.email.controller;

import com.sunwayworld.framework.controller.GenericDetailPageController;
import com.sunwayworld.framework.controller.GenericSearchListPageController;
import org.springframework.web.bind.annotation.RequestMapping;

@RequestMapping(value={"/secure/core/module/item/emails/page"})
public interface CoreEmailController
extends GenericSearchListPageController,
GenericDetailPageController {
}
