package com.sunwayworld.emc.module.mdm.basemdm.unit.resource.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.sunwayworld.emc.module.mdm.basemdm.unit.bean.EmcUnitConvertBean;
import com.sunwayworld.emc.module.mdm.basemdm.unit.resource.EmcUnitResource;
import com.sunwayworld.emc.module.mdm.basemdm.unit.service.EmcUnitService;
import com.sunwayworld.framework.data.page.Page;
import com.sunwayworld.framework.log.annotation.Log;
import com.sunwayworld.framework.log.annotation.LogModule;
import com.sunwayworld.framework.log.annotation.LogType;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.spring.annotation.GikamBean;

@LogModule(value = "计量单位")
@RestController
@GikamBean
public class EmcUnitResourceImpl implements EmcUnitResource {
    @Autowired
    private EmcUnitService emcUnitService;

    @Override
    public EmcUnitService getService() {
        return emcUnitService;
    }

    @Override
    @Log(value = "新增计量单位", type = LogType.INSERT)
    @RequestMapping(method = RequestMethod.POST)
    public String insert(RestJsonWrapperBean jsonWrapper) {
        return getService().insert(jsonWrapper);
    }

    @Override
    @Log(value = "通过单位查询转换因子", type = LogType.SELECT)
    public Page<EmcUnitConvertBean> selectConvertPaginationByUnit(@PathVariable String id, RestJsonWrapperBean wrapper) {
        return emcUnitService.selectConvertPaginationByUnit(id, wrapper);
    }

    @Override
    @Log(value = "新增单位转换规则", type = LogType.INSERT)
    @RequestMapping(value = "/action/insert-unit-convert", method = RequestMethod.POST)
    public void insertUnitConvert(RestJsonWrapperBean wrapper) {
        emcUnitService.insertUnitConvert(wrapper);
    }

    @Override
    @Log(value = "新增单位转换规则", type = LogType.DELETE)
    @RequestMapping(value = "/action/delete-unit-convert", method = RequestMethod.DELETE)
    public void deleteUnitConvert(RestJsonWrapperBean wrapper) {
        emcUnitService.deleteUnitConvert(wrapper);
    }
}
