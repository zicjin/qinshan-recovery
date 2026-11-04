/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.module.sys.todo.resource.impl;

import com.sunwayworld.framework.log.annotation.Log;
import com.sunwayworld.framework.log.annotation.LogModule;
import com.sunwayworld.framework.log.annotation.LogType;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import com.sunwayworld.framework.validator.data.annotation.ValidateDataWith;
import com.sunwayworld.module.sys.todo.bean.CoreTodoConfigIconDTO;
import com.sunwayworld.module.sys.todo.resource.CoreTodoConfigResource;
import com.sunwayworld.module.sys.todo.service.CoreTodoConfigService;
import com.sunwayworld.module.sys.todo.validator.CoreTodoConfigActivateValidator;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@GikamBean
@RestController
@LogModule(value="\u5f85\u529e\u914d\u7f6e")
public class CoreTodoConfigResourceImpl
implements CoreTodoConfigResource {
    @Autowired
    private CoreTodoConfigService configService;

    @Override
    public CoreTodoConfigService getService() {
        return this.configService;
    }

    @Override
    @Log(value="\u542f\u7528", type=LogType.ACTIVATE)
    @RequestMapping(value={"/action/activate"}, method={RequestMethod.PUT})
    @ValidateDataWith(value=CoreTodoConfigActivateValidator.class)
    public void activate(RestJsonWrapperBean wrapper) {
        CoreTodoConfigResource.super.activate(wrapper);
    }

    @Override
    @Log(value="\u67e5\u8be2\u5f85\u529e\u56fe\u6807", type=LogType.SELECT)
    public List<CoreTodoConfigIconDTO> selectIconList() {
        return this.getService().selectIconList();
    }
}
