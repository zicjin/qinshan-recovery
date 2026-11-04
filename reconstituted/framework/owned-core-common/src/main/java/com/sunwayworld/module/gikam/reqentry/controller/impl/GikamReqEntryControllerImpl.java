/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.module.gikam.reqentry.controller.impl;

import com.sunwayworld.framework.spring.annotation.GikamBean;
import com.sunwayworld.module.gikam.reqentry.controller.GikamReqEntryController;
import org.springframework.stereotype.Controller;

@GikamBean
@Controller
public class GikamReqEntryControllerImpl
implements GikamReqEntryController {
    @Override
    public String choosePage() {
        return "module/gikam/reqentries/core-reqentry-choose-list";
    }
}
