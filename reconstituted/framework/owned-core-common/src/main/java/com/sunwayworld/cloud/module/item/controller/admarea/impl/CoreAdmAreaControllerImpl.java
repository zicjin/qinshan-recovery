/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.item.controller.admarea.impl;

import com.sunwayworld.cloud.module.item.controller.admarea.CoreAdmAreaController;
import com.sunwayworld.framework.log.annotation.Log;
import com.sunwayworld.framework.log.annotation.LogType;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import org.springframework.stereotype.Controller;

@Controller
@GikamBean
public class CoreAdmAreaControllerImpl
implements CoreAdmAreaController {
    @Override
    @Log(value="\u884c\u653f\u533a\u57df\u7f16\u5236\u9875\u9762", type=LogType.CONTROLLER)
    public String editListPage() {
        return "module/item/adm-areas/core-adm-area-edit-list";
    }
}
