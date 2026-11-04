/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.module.gikam.error.controller;

import org.springframework.web.bind.annotation.RequestMapping;

@RequestMapping(value={"/error"})
public interface GikamErrorController {
    @RequestMapping(value={"/thymeleaf"})
    public String thymeleafPage();
}
