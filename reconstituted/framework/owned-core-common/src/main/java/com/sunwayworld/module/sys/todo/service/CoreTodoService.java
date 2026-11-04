/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.module.sys.todo.service;

import com.sunwayworld.framework.context.LocalContextHelper;
import com.sunwayworld.framework.data.page.Page;
import com.sunwayworld.framework.exception.UnsupportedException;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.security.bean.LoginUser;
import com.sunwayworld.module.sys.todo.bean.CoreTodoConfigBean;
import com.sunwayworld.module.sys.todo.bean.CoreTodoDTO;
import java.util.List;
import java.util.Map;

public interface CoreTodoService {
    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    default public Page<CoreTodoDTO> selectTodoList(LoginUser loginUser, String category, boolean countOnly, boolean effectiveOnly) {
        try {
            LocalContextHelper.setUserLogin(loginUser);
            Page<CoreTodoDTO> page = this.selectCurrentUserTodoList(category, countOnly, effectiveOnly);
            return page;
        }
        finally {
            LocalContextHelper.removeUserLogin();
        }
    }

    public Page<CoreTodoDTO> selectCurrentUserTodoList(String var1, boolean var2, boolean var3);

    default public String getReplacedParam(String param) {
        throw new UnsupportedException("You should implements this method if required.");
    }

    public List<Map<String, Object>> selectCurrentUserMobileTodoList(RestJsonWrapperBean var1);

    public CoreTodoDTO selectCurrentUserTodo(CoreTodoConfigBean var1);

    public Page<CoreTodoDTO> selectTodoListByUser(String var1);
}
