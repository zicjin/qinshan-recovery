/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.user.user.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping(value={"/open/core/module/mdm/user-passwords/page"})
public interface CoreUserPasswordController {
    public String passwordAccountPage();

    public String passwordResetPage();

    public String passwordCompletePage();

    public String sendCompletePage();

    public String urlInvalidPage();

    public String passwordToResetPage();
}
