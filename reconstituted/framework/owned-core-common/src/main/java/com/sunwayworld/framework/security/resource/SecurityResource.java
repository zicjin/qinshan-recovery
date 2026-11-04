/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.framework.security.resource;

import com.sunwayworld.framework.security.bean.CoreLicenseDTO;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@RequestMapping(value={"/open/security"})
public interface SecurityResource {
    @RequestMapping(value={"/captcha-images"}, method={RequestMethod.GET})
    public void captchaImage();

    @RequestMapping(value={"/licenses"}, method={RequestMethod.GET})
    public CoreLicenseDTO getLicense();
}
