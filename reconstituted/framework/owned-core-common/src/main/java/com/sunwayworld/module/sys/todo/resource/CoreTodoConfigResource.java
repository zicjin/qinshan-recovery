/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.module.sys.todo.resource;

import com.sunwayworld.framework.support.activatable.resource.GenericActivatableResource;
import com.sunwayworld.framework.support.base.resource.GenericResource;
import com.sunwayworld.module.sys.todo.bean.CoreTodoConfigBean;
import com.sunwayworld.module.sys.todo.bean.CoreTodoConfigIconDTO;
import com.sunwayworld.module.sys.todo.service.CoreTodoConfigService;
import java.util.List;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@RequestMapping(value={"/secure/core/module/sys/todo-configs"})
public interface CoreTodoConfigResource
extends GenericResource<CoreTodoConfigService, CoreTodoConfigBean, Long>,
GenericActivatableResource<CoreTodoConfigService, CoreTodoConfigBean, Long> {
    @RequestMapping(value={"/icons/queries"}, method={RequestMethod.GET})
    public List<CoreTodoConfigIconDTO> selectIconList();
}
