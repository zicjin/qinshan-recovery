/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.framework.qrcode.resource;

import com.sunwayworld.framework.log.annotation.Log;
import com.sunwayworld.framework.log.annotation.LogType;
import org.springframework.http.HttpStatus;
import org.springframework.lang.Nullable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

@RequestMapping(value={"/secure/core/qrcodes"})
public interface SunwayQRCodeResource {
    @Log(value="\u83b7\u53d6\u4e8c\u7ef4\u7801", type=LogType.SELECT)
    @RequestMapping(value={"/"}, method={RequestMethod.GET})
    @ResponseStatus(value=HttpStatus.OK)
    @ResponseBody
    public void getQRCode(@RequestParam String var1, @Nullable @RequestParam(required=false) Integer var2, @Nullable @RequestParam(required=false) Integer var3);
}
