/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.module.item.helpdocument.controller;

import com.sunwayworld.framework.controller.GenericEditListPageController;
import com.sunwayworld.framework.controller.GenericSearchListPageController;
import org.springframework.web.bind.annotation.RequestMapping;

@RequestMapping(value={"/secure/module/item/help-documents/page"})
public interface CoreHelpDocumentCatalogController
extends GenericEditListPageController,
GenericSearchListPageController {
}
