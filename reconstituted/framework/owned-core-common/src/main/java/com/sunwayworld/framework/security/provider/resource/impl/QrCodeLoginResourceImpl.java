/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.framework.security.provider.resource.impl;

import com.sunwayworld.framework.security.bean.QrCodeLoginDTO;
import com.sunwayworld.framework.security.bean.QrCodeLoginStatusDTO;
import com.sunwayworld.framework.security.bean.QrCodeScanStatusDTO;
import com.sunwayworld.framework.security.provider.resource.QrCodeLoginResource;
import com.sunwayworld.framework.security.provider.service.QrCodeLoginService;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

@GikamBean
@RestController
public class QrCodeLoginResourceImpl
implements QrCodeLoginResource {
    @Autowired
    private QrCodeLoginService loginService;

    @Override
    public QrCodeLoginDTO getQrCode() {
        return this.loginService.getQrCode();
    }

    @Override
    public QrCodeLoginStatusDTO getLoginStatus(String qrToken) {
        return this.loginService.getLoginStatus(qrToken);
    }

    @Override
    public QrCodeScanStatusDTO scan() {
        return this.loginService.scan();
    }

    @Override
    public QrCodeScanStatusDTO confirm() {
        return this.loginService.confirm();
    }
}
