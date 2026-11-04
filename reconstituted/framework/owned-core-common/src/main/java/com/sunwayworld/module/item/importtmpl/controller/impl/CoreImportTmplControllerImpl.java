/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.module.item.importtmpl.controller.impl;

import com.sunwayworld.framework.log.annotation.Log;
import com.sunwayworld.framework.log.annotation.LogType;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import com.sunwayworld.module.item.importtmpl.controller.CoreImportTmplController;
import org.springframework.stereotype.Controller;

@Controller
@GikamBean
public class CoreImportTmplControllerImpl
implements CoreImportTmplController {
    @Override
    @Log(value="\u5bfc\u5165\u6a21\u7248\u7f16\u5236\u5217\u8868\u9875", type=LogType.CONTROLLER)
    public String editListPage() {
        return "module/item/import-tmpls/core-import-tmpl-edit-list";
    }
}
