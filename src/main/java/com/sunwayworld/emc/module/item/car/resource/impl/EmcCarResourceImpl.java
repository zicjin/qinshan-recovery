package com.sunwayworld.emc.module.item.car.resource.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.sunwayworld.emc.module.item.car.bean.EmcCarBean;
import com.sunwayworld.emc.module.item.car.resource.EmcCarResource;
import com.sunwayworld.emc.module.item.car.service.EmcCarService;
import com.sunwayworld.framework.data.page.Page;
import com.sunwayworld.framework.log.annotation.Log;
import com.sunwayworld.framework.log.annotation.LogModule;
import com.sunwayworld.framework.log.annotation.LogType;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.spring.annotation.GikamBean;

/**
 * @author cuikx@sunwayworld.com
 * @date 2019-11-12
 */

@LogModule("汽车使用归还")
@RestController
@GikamBean
public class EmcCarResourceImpl implements EmcCarResource {
    @Autowired
    private EmcCarService carService;

    @Override
    public EmcCarService getService() {
        return carService;
    }

    @Log(value = "用车申请查询", type = LogType.SELECT)
    @Override
    @RequestMapping(value = "/queries", method = RequestMethod.POST)
    public Page<EmcCarBean> selectPagination(RestJsonWrapperBean wrapper) {
        return getService().selectPagination(wrapper);
    }

    @Log(value = "新增汽车使用归还", type = LogType.INSERT)
    @Override
    @RequestMapping(method = RequestMethod.POST)
    public Long insert(RestJsonWrapperBean wrapper) {
        return getService().insert(wrapper);
    }
}
