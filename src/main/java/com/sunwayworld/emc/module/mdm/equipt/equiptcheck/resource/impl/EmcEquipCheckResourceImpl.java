package com.sunwayworld.emc.module.mdm.equipt.equiptcheck.resource.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.sunwayworld.emc.module.mdm.equipt.equiptcheck.bean.EmcEquipDataBean;
import com.sunwayworld.emc.module.mdm.equipt.equiptcheck.bean.EmcEquipPartBean;
import com.sunwayworld.emc.module.mdm.equipt.equiptcheck.resource.EmcEquipCheckResource;
import com.sunwayworld.emc.module.mdm.equipt.equiptcheck.service.EmcEquipCheckService;
import com.sunwayworld.emc.module.mdm.equipt.equiptcheck.service.EmcEquipDataService;
import com.sunwayworld.emc.module.mdm.equipt.equiptcheck.service.EmcEquipPartService;
import com.sunwayworld.framework.data.page.Page;
import com.sunwayworld.framework.log.annotation.Log;
import com.sunwayworld.framework.log.annotation.LogModule;
import com.sunwayworld.framework.log.annotation.LogType;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.spring.annotation.GikamBean;

@LogModule("仪器验收申请")
@RestController
@GikamBean
public class EmcEquipCheckResourceImpl implements EmcEquipCheckResource {

    @Autowired
    private EmcEquipCheckService emcEquipCheckService;

    @Autowired
    private EmcEquipPartService emcEquipPartService;

    @Autowired
    private EmcEquipDataService emcEquipDataService;

    @Override
    public EmcEquipCheckService getService() {
        return emcEquipCheckService;
    }

    @Log(value = "新增仪器验收申请", type = LogType.INSERT)
    @Override
    @RequestMapping(method = RequestMethod.POST)
    public Long insert(RestJsonWrapperBean wrapper) {
        return getService().insert(wrapper);
    }

    @Log(value = "查询仪器部件", type = LogType.SELECT)
    @Override
    public Page<EmcEquipPartBean> selectEquiptPartPaginationById(Long id, RestJsonWrapperBean wrapper) {
        return emcEquipPartService.selectEquiptPartPaginationById(id, wrapper);
    }

    @Log(value = "新增仪器部件", type = LogType.INSERT)
    @Override
    public Long insertEquiptPart(Long id, RestJsonWrapperBean wrapper) {
        return emcEquipPartService.insertEquiptPart(id, wrapper);
    }

    @Log(value = "删除仪器部件", type = LogType.DELETE)
    @Override
    public void deleteEquiptPart(RestJsonWrapperBean wrapper) {
        emcEquipPartService.delete(wrapper);
    }

    @Log(value = "查询仪器资料", type = LogType.SELECT)
    @Override
    public Page<EmcEquipDataBean> selectEquiptDataPaginationById(Long id, RestJsonWrapperBean wrapper) {
        return emcEquipDataService.selectEquiptDataPaginationById(id, wrapper);
    }

    @Log(value = "新增仪器资料", type = LogType.INSERT)
    @Override
    public Long insertEquiptData(Long id, RestJsonWrapperBean wrapper) {
        return emcEquipDataService.insertEquiptData(id, wrapper);
    }

    @Log(value = "删除仪器资料", type = LogType.DELETE)
    @Override
    public void deleteEquiptData(RestJsonWrapperBean wrapper) {
        emcEquipDataService.delete(wrapper);
    }
}
