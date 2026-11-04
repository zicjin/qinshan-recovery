/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.module.sys.menu.resource.impl;

import com.sunwayworld.framework.data.page.Page;
import com.sunwayworld.framework.log.annotation.Log;
import com.sunwayworld.framework.log.annotation.LogModule;
import com.sunwayworld.framework.log.annotation.LogType;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import com.sunwayworld.module.sys.menu.bean.CoreMenuGroupLineRepresentation;
import com.sunwayworld.module.sys.menu.resource.CoreMenuGroupResource;
import com.sunwayworld.module.sys.menu.service.CoreMenuGroupService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@LogModule(value="\u7cfb\u7edf\u83dc\u5355\u7ec4")
@RestController
@GikamBean
public class CoreMenuGroupResourceImpl
implements CoreMenuGroupResource {
    @Autowired
    private CoreMenuGroupService menuGroupService;

    @Override
    public CoreMenuGroupService getService() {
        return this.menuGroupService;
    }

    @Override
    @Log(value="\u65b0\u589e", type=LogType.INSERT)
    public String insert(RestJsonWrapperBean wrapper) {
        return this.getService().insert(wrapper);
    }

    @Override
    @Log(value="\u65b0\u589e\u83dc\u5355", type=LogType.INSERT)
    public void insertLines(@PathVariable String id, RestJsonWrapperBean wrapper) {
        this.getService().insertLines(id, wrapper);
    }

    @Override
    @Log(value="\u5220\u9664\u83dc\u5355", type=LogType.DELETE)
    public void deleteLines(String id, RestJsonWrapperBean wrapper) {
        this.getService().deleteLines(id, wrapper);
    }

    @Override
    @Log(value="\u67e5\u8be2\u83dc\u5355", type=LogType.SELECT)
    public Page<CoreMenuGroupLineRepresentation> selectLineTreePagination(@PathVariable String id, RestJsonWrapperBean wrapper) {
        return this.getService().selectLineTreePagination(id, wrapper);
    }
}
