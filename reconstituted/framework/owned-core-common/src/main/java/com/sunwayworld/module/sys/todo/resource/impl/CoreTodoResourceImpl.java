/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.module.sys.todo.resource.impl;

import com.sunwayworld.framework.data.page.Page;
import com.sunwayworld.framework.log.annotation.LogModule;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import com.sunwayworld.module.sys.todo.bean.CoreTodoDTO;
import com.sunwayworld.module.sys.todo.resource.CoreTodoResource;
import com.sunwayworld.module.sys.todo.service.CoreTodoService;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@LogModule(value="\u5f85\u529e")
@RestController
@GikamBean
public class CoreTodoResourceImpl
implements CoreTodoResource {
    @Autowired
    private CoreTodoService todoService;

    @Override
    public Page<CoreTodoDTO> selectCurrentUserTodoList(@RequestParam(required=false) String category, @RequestParam(defaultValue="true") Boolean countOnly, @RequestParam(defaultValue="true") Boolean effectiveOnly) {
        return this.todoService.selectCurrentUserTodoList(category, countOnly, effectiveOnly);
    }

    @Override
    public List<Map<String, Object>> selectCurrentUserMobileTodoList(RestJsonWrapperBean wrapperBean) {
        return this.todoService.selectCurrentUserMobileTodoList(wrapperBean);
    }
}
