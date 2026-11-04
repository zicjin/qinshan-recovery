/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.item.controller.pvconf.impl;

import com.sunwayworld.cloud.module.item.controller.pvconf.CoreProcessViewConfController;
import com.sunwayworld.framework.log.annotation.Log;
import com.sunwayworld.framework.log.annotation.LogType;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import org.springframework.stereotype.Controller;

@Controller
@GikamBean
public class CoreProcessViewConfControllerImpl
implements CoreProcessViewConfController {
    @Override
    @Log(value="\u6570\u636e\u53ef\u89c6\u5316\u914d\u7f6e\u7f16\u5236\u5217\u8868\u9875", type=LogType.CONTROLLER)
    public String editListPage() {
        return "module/item/pv-confs/core-process-view-conf-edit-list";
    }

    @Override
    @Log(value="\u5217\u8868\u6570\u636e\u914d\u7f6e\u9875", type=LogType.CONTROLLER)
    public String gridConfPage() {
        return "module/item/pv-confs/core-process-view-grid-conf";
    }

    @Override
    @Log(value="SQL\u914d\u7f6e\u9875", type=LogType.CONTROLLER)
    public String editSqlPage() {
        return "module/item/pv-confs/core-process-view-edit-sql";
    }
}
