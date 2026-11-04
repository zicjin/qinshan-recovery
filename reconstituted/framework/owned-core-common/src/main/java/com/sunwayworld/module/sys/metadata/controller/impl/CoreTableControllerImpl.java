/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.module.sys.metadata.controller.impl;

import com.sunwayworld.framework.spring.annotation.GikamBean;
import com.sunwayworld.module.sys.metadata.controller.CoreTableController;
import org.springframework.stereotype.Controller;

@Controller
@GikamBean
public class CoreTableControllerImpl
implements CoreTableController {
    @Override
    public String choosePage() {
        return "module/sys/tables/core-table-choose-list";
    }

    @Override
    public String columnChoosePage() {
        return "module/sys/tables/core-table-column-choose-list";
    }
}
