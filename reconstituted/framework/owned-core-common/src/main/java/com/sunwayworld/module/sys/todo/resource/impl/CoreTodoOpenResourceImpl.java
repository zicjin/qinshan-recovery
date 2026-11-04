/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.module.sys.todo.resource.impl;

import com.sunwayworld.framework.data.page.Page;
import com.sunwayworld.framework.log.annotation.LogModule;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import com.sunwayworld.module.sys.todo.bean.CoreTodoDTO;
import com.sunwayworld.module.sys.todo.resource.CoreTodoOpenResource;
import com.sunwayworld.module.sys.todo.service.CoreTodoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@LogModule(value="\u5f85\u529e")
@RestController
@GikamBean
public class CoreTodoOpenResourceImpl
implements CoreTodoOpenResource {
    @Autowired
    private CoreTodoService todoService;

    @Override
    public Page<CoreTodoDTO> selectTodoListByUser(@RequestParam String userId) {
        return this.todoService.selectTodoListByUser(userId);
    }
}
