/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.framework.qrcode.resource.impl;

import com.sunwayworld.framework.log.annotation.LogModule;
import com.sunwayworld.framework.qrcode.QRCodeManager;
import com.sunwayworld.framework.qrcode.resource.SunwayQRCodeResource;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import com.sunwayworld.framework.utils.ServletUtils;
import org.springframework.lang.Nullable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@LogModule(value="\u4e8c\u7ef4\u7801")
@GikamBean
@RestController
public class SunwayQRCodeResourceImpl
implements SunwayQRCodeResource {
    @Override
    public void getQRCode(@RequestParam String content, @Nullable @RequestParam(required=false) Integer width, @Nullable @RequestParam(required=false) Integer height) {
        QRCodeManager.write(ServletUtils.getCurrentResponse(), content, width, height);
    }
}
