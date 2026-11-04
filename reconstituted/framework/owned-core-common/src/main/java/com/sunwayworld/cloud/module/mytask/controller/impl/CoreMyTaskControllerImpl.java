/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.mytask.controller.impl;

import com.sunwayworld.cloud.module.mytask.controller.CoreMyTaskController;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import org.springframework.stereotype.Controller;

@GikamBean
@Controller
public class CoreMyTaskControllerImpl
implements CoreMyTaskController {
    @Override
    public String editListPage() {
        return "module/my-tasks/core-my-task-edit-list";
    }
}
