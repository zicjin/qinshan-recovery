/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.module.sys.todo.service;

import com.sunwayworld.framework.support.activatable.service.GenericActivatableService;
import com.sunwayworld.framework.support.base.service.GenericService;
import com.sunwayworld.module.sys.todo.bean.CoreTodoConfigBean;
import com.sunwayworld.module.sys.todo.bean.CoreTodoConfigIconDTO;
import java.util.List;

public interface CoreTodoConfigService
extends GenericService<CoreTodoConfigBean, Long>,
GenericActivatableService<CoreTodoConfigBean, Long> {
    public List<CoreTodoConfigBean> selectActivatedConfigList();

    public List<CoreTodoConfigIconDTO> selectIconList();
}
