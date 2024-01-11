package com.sunwayworld.emc.module.bp.sample.condition.resource.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.sunwayworld.emc.module.bp.sample.condition.bean.EmcWorkConditionLineBean;
import com.sunwayworld.emc.module.bp.sample.condition.resource.EmcWorkConditionResource;
import com.sunwayworld.emc.module.bp.sample.condition.service.EmcWorkConditionService;
import com.sunwayworld.framework.data.page.Page;
import com.sunwayworld.framework.log.annotation.Log;
import com.sunwayworld.framework.log.annotation.LogModule;
import com.sunwayworld.framework.log.annotation.LogType;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.spring.annotation.GikamBean;

@LogModule("现场工况信息")
@RestController
@GikamBean
public class EmcWorkConditionResourceImpl implements EmcWorkConditionResource {

    @Autowired
    private EmcWorkConditionService emcWorkConditionService;

    @Override
    public EmcWorkConditionService getService() {
        return emcWorkConditionService;
    }

    @Log(value = "新增采样单编码信息", type = LogType.INSERT)
    @Override
    @RequestMapping(method = RequestMethod.POST)
    public Long insert(RestJsonWrapperBean wrapper) {
        return getService().insert(wrapper);
    }

    @Override
    @RequestMapping(value = "/lines/queries", method = RequestMethod.POST)
    @Log(value = "查询工况明细", type = LogType.SELECT)
    public Page<EmcWorkConditionLineBean> queryLineByPagination(RestJsonWrapperBean wrapper) {
        return getService().queryLineByPagination(wrapper);
    }

    @Override
    @RequestMapping(value = "/action/insert-line")
    @Log(value = "新增工况明细", type = LogType.INSERT)
    public void insertLine(RestJsonWrapperBean wrapper) {
        getService().insertLine(wrapper);
    }

    @Override
    @RequestMapping(value = "/action/delete-line")
    @Log(value = "删除工况明细", type = LogType.DELETE)
    public void deleteLine(RestJsonWrapperBean wrapper) {
        getService().deleteLine(wrapper);
    }
}
