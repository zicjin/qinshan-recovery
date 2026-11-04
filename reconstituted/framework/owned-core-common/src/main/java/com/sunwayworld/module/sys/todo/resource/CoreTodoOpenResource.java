/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.module.sys.todo.resource;

import com.sunwayworld.framework.data.page.Page;
import com.sunwayworld.module.sys.todo.bean.CoreTodoDTO;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

@RequestMapping(value={"/open/core/module/sys/todos"})
public interface CoreTodoOpenResource {
    @RequestMapping(method={RequestMethod.POST})
    public Page<CoreTodoDTO> selectTodoListByUser(@RequestParam String var1);
}
