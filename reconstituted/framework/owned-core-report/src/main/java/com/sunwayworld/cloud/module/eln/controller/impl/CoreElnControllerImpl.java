/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.eln.controller.impl;

import com.sunwayworld.cloud.module.eln.controller.CoreElnController;
import com.sunwayworld.framework.log.annotation.Log;
import com.sunwayworld.framework.log.annotation.LogType;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import com.sunwayworld.framework.utils.StringUtils;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Controller;

@Controller
@GikamBean
public class CoreElnControllerImpl
implements CoreElnController {
    @Override
    @Log(value="ELN\u5728\u7ebf\u8868\u683c\u7f16\u8f91\u5668", type=LogType.CONTROLLER)
    public String spreadJsWebDesignerPage() {
        String locale = LocaleContextHolder.getLocale().toString();
        if (StringUtils.startsWith(locale, "en")) {
            return "module/eln/elns/core-eln-spreadjs-web-designer-en";
        }
        return "module/eln/elns/core-eln-spreadjs-web-designer";
    }

    @Override
    @Log(value="ELN\u6a21\u677f\u7ed1\u5b9a\u6570\u636e\u6e90\u4fe1\u606f", type=LogType.CONTROLLER)
    public String bindDataSourcePage() {
        return "module/eln/elns/bind-data-source";
    }
}
