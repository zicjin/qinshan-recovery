/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.mobile.push.controller;

import com.sunwayworld.framework.controller.GenericChoosePageController;
import com.sunwayworld.framework.controller.GenericEditListPageController;
import org.springframework.web.bind.annotation.RequestMapping;

@RequestMapping(value={"/secure/core/module/sys/mobile/pushs/page"})
public interface MobilePushController
extends GenericEditListPageController,
GenericChoosePageController {
}
