/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.framework.security.resource.impl;

import com.sunwayworld.framework.context.ApplicationContextHelper;
import com.sunwayworld.framework.security.bean.CoreLicenseDTO;
import com.sunwayworld.framework.security.captcha.SecurityCaptchaService;
import com.sunwayworld.framework.security.resource.SecurityResource;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import java.time.LocalDate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

@GikamBean
@RestController
public class SecurityResourceImpl
implements SecurityResource {
    @Autowired
    private SecurityCaptchaService captchaService;

    @Override
    public void captchaImage() {
        this.captchaService.captchaImage();
    }

    @Override
    public CoreLicenseDTO getLicense() {
        CoreLicenseDTO license = new CoreLicenseDTO();
        LocalDate expiredDate = ApplicationContextHelper.getLicenseDeadline();
        if (expiredDate.isBefore(LocalDate.now().plusMonths(6L))) {
            license.setExpiredDate(expiredDate);
        }
        license.setLoginLimit(Integer.valueOf(ApplicationContextHelper.getLoginUserLimitation()).longValue());
        return license;
    }
}
