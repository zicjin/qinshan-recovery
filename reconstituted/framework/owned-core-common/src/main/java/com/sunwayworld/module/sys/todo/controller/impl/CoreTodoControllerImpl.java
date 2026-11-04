/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.module.sys.todo.controller.impl;

import com.sunwayworld.framework.spring.annotation.GikamBean;
import com.sunwayworld.module.sys.todo.controller.CoreTodoController;
import org.springframework.stereotype.Controller;

@Controller
@GikamBean
public class CoreTodoControllerImpl
implements CoreTodoController {
    @Override
    public String queryListPage() {
        return "module/sys/todos/core-todo-query-list";
    }
}
