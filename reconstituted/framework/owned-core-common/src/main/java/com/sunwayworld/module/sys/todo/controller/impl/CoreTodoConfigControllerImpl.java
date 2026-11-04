/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.module.sys.todo.controller.impl;

import com.sunwayworld.framework.spring.annotation.GikamBean;
import com.sunwayworld.module.sys.todo.controller.CoreTodoConfigController;
import org.springframework.stereotype.Controller;

@Controller
@GikamBean
public class CoreTodoConfigControllerImpl
implements CoreTodoConfigController {
    @Override
    public String editListPage() {
        return "module/sys/todo-configs/core-todo-config-edit-list";
    }
}
