package com.sunwayworld.emc.module.item.lm.overtime.resource.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.sunwayworld.emc.module.item.lm.overtime.bean.EmcOvertimeBean;
import com.sunwayworld.emc.module.item.lm.overtime.resource.EmcOvertimeResource;
import com.sunwayworld.emc.module.item.lm.overtime.service.EmcOvertimeService;
import com.sunwayworld.framework.data.page.Page;
import com.sunwayworld.framework.log.annotation.Log;
import com.sunwayworld.framework.log.annotation.LogModule;
import com.sunwayworld.framework.log.annotation.LogType;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.spring.annotation.GikamBean;

/**
 * @author cuikx@sunwayworld.com
 * @date 2019-11-14
 */

@LogModule("加班表")
@RestController
@GikamBean
public class EmcOvertimeResourceImpl implements EmcOvertimeResource {
    @Autowired
    private EmcOvertimeService overtimeService;

    @Override
    public EmcOvertimeService getService() {
        return overtimeService;
    }

    @Log(value = "新增加班表", type = LogType.INSERT)
    @Override
    @RequestMapping(method = RequestMethod.POST)
    public Long insert(RestJsonWrapperBean wrapper) {
        return getService().insert(wrapper);
    }

    @Log(value = "用车申请查询", type = LogType.SELECT)
    @Override
    @RequestMapping(value = "/queries", method = RequestMethod.POST)
    public Page<EmcOvertimeBean> selectPagination(RestJsonWrapperBean wrapper) {
        return getService().selectPagination(wrapper);
    }
}
