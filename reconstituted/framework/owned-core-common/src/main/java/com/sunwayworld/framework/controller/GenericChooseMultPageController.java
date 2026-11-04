/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.framework.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public interface GenericChooseMultPageController {
    @RequestMapping(value={"/choose-mult-list"})
    public String chooseMultPage();
}
