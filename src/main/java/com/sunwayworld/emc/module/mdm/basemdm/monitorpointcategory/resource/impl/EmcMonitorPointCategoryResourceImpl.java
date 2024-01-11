package com.sunwayworld.emc.module.mdm.basemdm.monitorpointcategory.resource.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.sunwayworld.emc.module.mdm.basemdm.monitorpointcategory.bean.EmcMonitorPointCategoryBean;
import com.sunwayworld.emc.module.mdm.basemdm.monitorpointcategory.bean.EmcMpcTmplBean;
import com.sunwayworld.emc.module.mdm.basemdm.monitorpointcategory.resource.EmcMonitorPointCategoryResource;
import com.sunwayworld.emc.module.mdm.basemdm.monitorpointcategory.service.EmcMonitorPointCategoryService;
import com.sunwayworld.framework.data.page.Page;
import com.sunwayworld.framework.log.annotation.Log;
import com.sunwayworld.framework.log.annotation.LogModule;
import com.sunwayworld.framework.log.annotation.LogType;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.spring.annotation.GikamBean;

/**
 * @author magh@sunwayworld.com
 * @date 2019-09-10
 */
@LogModule("监测点类型")
@RestController
@GikamBean
public class EmcMonitorPointCategoryResourceImpl implements EmcMonitorPointCategoryResource {
    @Autowired
    private EmcMonitorPointCategoryService monitorPointCategoryService;

    @Override
    public EmcMonitorPointCategoryService getService() {
        return monitorPointCategoryService;
    }

    @Log(value = "新增监测点类型", type = LogType.INSERT)
    @Override
    @RequestMapping(method = RequestMethod.POST)
    public String insert(RestJsonWrapperBean wrapper) {
        return getService().insert(wrapper);
    }

    @Override
    @Log(value = "查询监测模板", type = LogType.SELECT)
    public Page<EmcMpcTmplBean> selectTmplPaginationByMpcId(String id, RestJsonWrapperBean wrapper) {
        return getService().selectTmplPaginationByMpcId(id, wrapper);
    }

    @Override
    @Log(value = "新增监测模板", type = LogType.INSERT)
    public Long insertTmpl(String id, RestJsonWrapperBean wrapper) {
        return getService().insertTmpl(id, wrapper);
    }

    @Override
    @Log(value = "删除监测模板", type = LogType.DELETE)
    public void deleteTmpl(String id, RestJsonWrapperBean wrapper) {
        getService().deleteTmpl(id, wrapper);
    }

    @Override
    @Log(value = "监测点类型过滤监测模版组", type = LogType.SELECT)
    public List<EmcMonitorPointCategoryBean> selectFilterMonitorTmplGroupSelectableList() {
        return getService().selectFilterMonitorTmplGroupSelectableList();
    }

    @Override
    public List<EmcMpcTmplBean> selectTmplSelectableList() {
        return getService().selectTmplSelectableList();
    }

}
