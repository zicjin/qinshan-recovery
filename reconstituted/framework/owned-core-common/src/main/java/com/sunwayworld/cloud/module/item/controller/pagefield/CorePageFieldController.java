/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.item.controller.pagefield;

import com.sunwayworld.framework.controller.GenericChoosePageController;
import com.sunwayworld.framework.controller.GenericEditListPageController;
import org.springframework.web.bind.annotation.RequestMapping;

@RequestMapping(value={"/secure/module/item/page-fields/page"})
public interface CorePageFieldController
extends GenericEditListPageController,
GenericChoosePageController {
}
