package com.sunwayworld.emc.module.mdm.basemdm.monitortmplgroup.resource.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

import com.sunwayworld.emc.module.mdm.basemdm.monitortmplgroup.bean.EmcOrderDescBean;
import com.sunwayworld.emc.module.mdm.basemdm.monitortmplgroup.resource.EmcMonitorTmplGroupResource;
import com.sunwayworld.emc.module.mdm.basemdm.monitortmplgroup.service.EmcMonitorTmplGroupService;
import com.sunwayworld.framework.data.page.Page;
import com.sunwayworld.framework.log.annotation.Log;
import com.sunwayworld.framework.log.annotation.LogModule;
import com.sunwayworld.framework.log.annotation.LogType;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.spring.annotation.GikamBean;

/**
 * @author magh@sunwayworld.com 2019年9月6日
 */

@LogModule("监测模板组")
@RestController
@GikamBean
public class EmcMonitorTmplGroupResourceImpl implements EmcMonitorTmplGroupResource {
    @Autowired
    private EmcMonitorTmplGroupService monitorTmplGroupService;

    @Override
    public EmcMonitorTmplGroupService getService() {
        return monitorTmplGroupService;
    }

    @Log(value = "新增监测模板组", type = LogType.INSERT)
    @Override
    public Long insert(RestJsonWrapperBean wrapper) {
        return getService().insert(wrapper);
    }

    @Log(value = "根据监测模板组ID查询样品性状描述", type = LogType.SELECT)
    @Override
    public Page<EmcOrderDescBean> selectOrderDescPaginationByMonitorTmplGroupId(Long id, RestJsonWrapperBean wrapper) {
        return getService().selectOrderDescPaginationByMonitorTmplGroupId(id, wrapper);
    }

    @Log(value = "新增样品性状描述", type = LogType.INSERT)
    @Override
    public Long insertOrderDesc(Long id, RestJsonWrapperBean wrapper) {
        return getService().insertOrderDesc(id, wrapper);
    }

    @Log(value = "删除样品性状描述", type = LogType.INSERT)
    @Override
    public void deleteOrderDesc(Long id, RestJsonWrapperBean wrapper) {
        getService().deleteOrderDesc(id, wrapper);
    }

    @Log(value = "启用样品性状描述", type = LogType.ACTIVATE)
    @Override
    public void activateOrderDesc(RestJsonWrapperBean wrapper) {
        getService().activateOrderDesc(wrapper);
        ;
    }

    @Log(value = "停用样品性状描述", type = LogType.ACTIVATE)
    @Override
    public void deactivateOrderDesc(RestJsonWrapperBean wrapper) {
        getService().deactivateOrderDesc(wrapper);
    }
}
