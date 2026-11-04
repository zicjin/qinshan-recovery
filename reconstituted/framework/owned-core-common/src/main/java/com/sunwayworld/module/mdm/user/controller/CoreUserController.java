/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.module.mdm.user.controller;

import com.sunwayworld.framework.controller.GenericChooseMultPageController;
import com.sunwayworld.framework.controller.GenericChoosePageController;
import com.sunwayworld.framework.controller.GenericDetailPageController;
import com.sunwayworld.framework.controller.GenericEditListPageController;
import org.springframework.web.bind.annotation.RequestMapping;

@RequestMapping(value={"/secure/core/module/mdm/users/page"})
public interface CoreUserController
extends GenericEditListPageController,
GenericDetailPageController,
GenericChooseMultPageController,
GenericChoosePageController {
    public String changePassword();
}
