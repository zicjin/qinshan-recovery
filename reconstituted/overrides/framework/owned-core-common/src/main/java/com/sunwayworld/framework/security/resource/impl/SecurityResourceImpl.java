/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.framework.security.resource.impl;

import com.sunwayworld.framework.security.captcha.SecurityCaptchaService;
import com.sunwayworld.framework.security.resource.SecurityResource;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import java.util.LinkedHashMap;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
    public ResponseEntity<Map<String, Object>> getLicense() {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("status", HttpStatus.NOT_FOUND.value());
        body.put("error", HttpStatus.NOT_FOUND.getReasonPhrase());
        body.put("path", "/open/security/licenses");
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(body);
    }
}
