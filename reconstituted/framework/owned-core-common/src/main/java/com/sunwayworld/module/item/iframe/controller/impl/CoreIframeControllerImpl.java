/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.module.item.iframe.controller.impl;

import com.sunwayworld.framework.spring.annotation.GikamBean;
import com.sunwayworld.module.item.iframe.controller.CoreIframeController;
import org.springframework.stereotype.Controller;

@Controller
@GikamBean
public class CoreIframeControllerImpl
implements CoreIframeController {
    @Override
    public String indexPage() {
        return "module/item/iframes/core-common-iframe";
    }

    @Override
    public String commonSinglePage() {
        return "module/item/iframes/comon-single";
    }
}
