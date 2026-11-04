/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.framework.controller;

import com.sunwayworld.framework.exception.NotImplementedException;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public interface GenericChoosePageController {
    @RequestMapping(value={"/choose-list"})
    public String choosePage();

    @RequestMapping(value={"/simple-choose-list"})
    default public String simpleChoosePage() {
        throw new NotImplementedException();
    }
}
