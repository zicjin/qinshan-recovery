/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.module.sys.todo.resource;

import com.sunwayworld.framework.data.page.Page;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.module.sys.todo.bean.CoreTodoDTO;
import java.util.List;
import java.util.Map;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

@RequestMapping(value={"/core/module/sys/todos"})
public interface CoreTodoResource {
    @RequestMapping(method={RequestMethod.POST})
    public Page<CoreTodoDTO> selectCurrentUserTodoList(@RequestParam(required=false) String var1, @RequestParam(defaultValue="true") Boolean var2, @RequestParam(defaultValue="true") Boolean var3);

    @RequestMapping(value={"/mobile"}, method={RequestMethod.POST})
    public List<Map<String, Object>> selectCurrentUserMobileTodoList(RestJsonWrapperBean var1);
}
