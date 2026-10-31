package com.sunwayworld.emc.module.bp.result.run.resource.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.sunwayworld.emc.module.bp.result.run.bean.EmcRunRemindBean;
import com.sunwayworld.emc.module.bp.result.run.resource.EmcRunRemindResource;
import com.sunwayworld.emc.module.bp.result.run.service.EmcRunRemindService;
import com.sunwayworld.framework.data.page.Page;
import com.sunwayworld.framework.log.annotation.Log;
import com.sunwayworld.framework.log.annotation.LogModule;
import com.sunwayworld.framework.log.annotation.LogType;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.spring.annotation.GikamBean;

@LogModule("批次提醒")
@RestController
@GikamBean
public class EmcRunRemindResourceImpl implements EmcRunRemindResource {

    @Autowired
    private EmcRunRemindService emcRunRemindService;

    @Override
    public EmcRunRemindService getService() {
        return emcRunRemindService;
    }

    @Log(value = "新增批次提醒", type = LogType.INSERT)
    @Override
    @RequestMapping(method = RequestMethod.POST)
    public Long insert(RestJsonWrapperBean wrapper) {
        return getService().insert(wrapper);
    }

    @Override
    @Log(value = "校验批数据信息", type = LogType.SELECT)
    @RequestMapping(value = "/queries/runRemind/{runId}", method = RequestMethod.POST)
    public Page<EmcRunRemindBean> selectRunRemind(@PathVariable String runId, RestJsonWrapperBean wrapper) {
        return getService().selectRunRemind(runId, wrapper);
    }

    @Override
    @Log(value = "校验批结果数据信息", type = LogType.SELECT)
    @RequestMapping(value = "/queries/resultRemind/{runId}", method = RequestMethod.POST)
    public Page<EmcRunRemindBean> selectResultRemind(@PathVariable String runId, RestJsonWrapperBean wrapper) {
        return getService().selectResultRemind(runId, wrapper);
    }
}
