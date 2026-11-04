/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.module.sys.code.controller.impl;

import com.sunwayworld.framework.log.annotation.Log;
import com.sunwayworld.framework.log.annotation.LogType;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import com.sunwayworld.module.sys.code.controller.CoreCodeCategoryController;
import org.springframework.stereotype.Controller;

@Controller
@GikamBean
public class CoreCodeCategoryControllerImpl
implements CoreCodeCategoryController {
    @Override
    @Log(value="\u5206\u7c7b\u7f16\u7801\u7f16\u5236\u9875\u9762", type=LogType.CONTROLLER)
    public String editListPage() {
        return "module/sys/code-categories/code-category-edit-list";
    }

    @Override
    @Log(value="\u5206\u7c7b\u7f16\u7801\u7f16\u5236\u8be6\u60c5\u9875\u9762", type=LogType.CONTROLLER)
    public String detailPage() {
        return "module/sys/code-categories/code-category-detail";
    }
}
