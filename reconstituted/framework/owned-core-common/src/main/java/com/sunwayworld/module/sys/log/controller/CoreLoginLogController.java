/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.module.sys.log.controller;

import com.sunwayworld.framework.controller.GenericSearchListPageController;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping(value={"/secure/core/module/sys/login-logs/page"})
public interface CoreLoginLogController
extends GenericSearchListPageController {
    @RequestMapping(value={"/online-list"})
    public String onlineListPage();
}
