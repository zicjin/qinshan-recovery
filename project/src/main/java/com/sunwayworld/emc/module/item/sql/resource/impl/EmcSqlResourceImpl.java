package com.sunwayworld.emc.module.item.sql.resource.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.sunwayworld.emc.module.item.sql.resource.EmcSqlResource;
import com.sunwayworld.emc.module.item.sql.service.EmcSqlService;
import com.sunwayworld.framework.log.annotation.Log;
import com.sunwayworld.framework.log.annotation.LogModule;
import com.sunwayworld.framework.log.annotation.LogType;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.spring.annotation.GikamBean;

@LogModule("sql管理")
@RestController
@GikamBean
public class EmcSqlResourceImpl implements EmcSqlResource {

    @Autowired
    private EmcSqlService emcSqlService;

    @Override
    public EmcSqlService getService() {
        return emcSqlService;
    }

    @Log(value = "新增sql管理", type = LogType.INSERT)
    @Override
    @RequestMapping(method = RequestMethod.POST)
    public Long insert(RestJsonWrapperBean wrapper) {
        return getService().insert(wrapper);
    }

}
