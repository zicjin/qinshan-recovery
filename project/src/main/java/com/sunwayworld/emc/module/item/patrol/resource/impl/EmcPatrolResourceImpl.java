package com.sunwayworld.emc.module.item.patrol.resource.impl;

import java.text.ParseException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.sunwayworld.emc.module.item.patrol.bean.EmcPatrolBean;
import com.sunwayworld.emc.module.item.patrol.resource.EmcPatrolResource;
import com.sunwayworld.emc.module.item.patrol.service.EmcPatrolService;
import com.sunwayworld.framework.data.page.Page;
import com.sunwayworld.framework.log.annotation.Log;
import com.sunwayworld.framework.log.annotation.LogModule;
import com.sunwayworld.framework.log.annotation.LogType;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.spring.annotation.GikamBean;

/**
 * @author cuikx@sunwayworld.com
 * @date 2019-11-09
 */

@LogModule("巡查记录表")
@RestController
@GikamBean
public class EmcPatrolResourceImpl implements EmcPatrolResource {
    @Autowired
    private EmcPatrolService patrolService;

    @Override
    public EmcPatrolService getService() {
        return patrolService;
    }

    @Log(value = "新增巡查记录表", type = LogType.INSERT)
    @Override
    @RequestMapping(method = RequestMethod.POST)
    public Long insert(RestJsonWrapperBean wrapper) {
        return getService().insert(wrapper);
    }

    @Log(value = "月增加", type = LogType.INSERT)
    @Override
    @RequestMapping(value = "/monthInsert", method = RequestMethod.POST)
    public void monthInsert(RestJsonWrapperBean wrapper) throws ParseException {
        getService().monthInsert(wrapper);
    }

    @Log(value = "月查询", type = LogType.SELECT)
    @Override
    @RequestMapping(value = "/queries/raw/month", method = RequestMethod.POST)
    public Page<EmcPatrolBean> selectMonthRawPagination(RestJsonWrapperBean wrapper) {
        return getService().selectMonthRawPagination(wrapper);
    }

    @Log(value = "根据巡查人月查询", type = LogType.SELECT)
    @Override
    @RequestMapping(value = "/queries/raw/month/patrolUser", method = RequestMethod.POST)
    public Page<EmcPatrolBean> selectMonthRawPaginationByPatrolUserId(RestJsonWrapperBean wrapper) {
        return getService().selectMonthRawPaginationByPatrolUserId(wrapper);
    }

    @Log(value = "根据处理人月查询", type = LogType.SELECT)
    @Override
    @RequestMapping(value = "/queries/raw/month/handleUser", method = RequestMethod.POST)
    public Page<EmcPatrolBean> selectMonthRawPaginationByHandleUserId(RestJsonWrapperBean wrapper) {
        return getService().selectMonthRawPaginationByHandleUserId(wrapper);
    }

}
