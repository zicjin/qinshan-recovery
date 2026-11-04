/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.framework.security.provider.resource;

import com.sunwayworld.framework.security.bean.QrCodeLoginDTO;
import com.sunwayworld.framework.security.bean.QrCodeLoginStatusDTO;
import com.sunwayworld.framework.security.bean.QrCodeScanStatusDTO;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@RequestMapping(value={"/open/qrcode-logins"})
public interface QrCodeLoginResource {
    @RequestMapping(value={"/qr-code"}, method={RequestMethod.GET})
    public QrCodeLoginDTO getQrCode();

    @RequestMapping(value={"/statuses/{qrToken}"}, method={RequestMethod.GET})
    public QrCodeLoginStatusDTO getLoginStatus(@PathVariable(value="qrToken") String var1);

    @RequestMapping(value={"/action/scan"}, method={RequestMethod.POST})
    public QrCodeScanStatusDTO scan();

    @RequestMapping(value={"/action/confirm"}, method={RequestMethod.POST})
    public QrCodeScanStatusDTO confirm();
}
