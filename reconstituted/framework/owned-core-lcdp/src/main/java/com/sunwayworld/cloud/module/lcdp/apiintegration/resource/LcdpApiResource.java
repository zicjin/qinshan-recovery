/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.lcdp.apiintegration.resource;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.web.bind.annotation.RequestMapping;

@RequestMapping(value={"/open/core/module/lcdp"})
public interface LcdpApiResource {
    public Object openApi(String var1, HttpServletRequest var2);

    public void openSoap(HttpServletRequest var1, HttpServletResponse var2) throws Exception;
}
