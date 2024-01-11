package com.sunwayworld.emc.module.mdm.basemdm.equiptrequisition.resource.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.sunwayworld.emc.module.mdm.basemdm.equiptrequisition.resource.EmcEquiptRequisitionResource;
import com.sunwayworld.emc.module.mdm.basemdm.equiptrequisition.service.EmcEquiptRequisitionService;
import com.sunwayworld.framework.log.annotation.Log;
import com.sunwayworld.framework.log.annotation.LogModule;
import com.sunwayworld.framework.log.annotation.LogType;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.spring.annotation.GikamBean;

@LogModule("仪器出入库")
@RestController
@GikamBean
public class EmcEquiptRequisitionResourceImpl implements EmcEquiptRequisitionResource {

    @Autowired
    private EmcEquiptRequisitionService emcEquiptRequisitionService;

    @Override
    public EmcEquiptRequisitionService getService() {
        return emcEquiptRequisitionService;
    }

    @Log(value = "新增仪器出入库", type = LogType.INSERT)
    @Override
    @RequestMapping(method = RequestMethod.POST)
    public Long insert(RestJsonWrapperBean wrapper) {
        return getService().insert(wrapper);
    }

    @Log(value = "仪器归还确认", type = LogType.UPDATE)
    @Override
    @RequestMapping(value = "/{ids}/confirmBack"  ,method = RequestMethod.POST)
    public void confirmBack(@PathVariable String ids,RestJsonWrapperBean wrapper) {
        getService().confirmBack(ids,wrapper);
    }

    @Log(value = "批量修改领用时状态", type = LogType.UPDATE)
    @Override
    @RequestMapping(value = "/{ids}/rentStatus"  ,method = RequestMethod.POST)
    public void batchChangeRentStatus(@PathVariable String ids, RestJsonWrapperBean wrapper) {
        getService().batchChangeRentStatus(ids,wrapper);
    }

    @Log(value = "批量修改归还时状态", type = LogType.UPDATE)
    @Override
    @RequestMapping(value = "/{ids}/backStatus"  ,method = RequestMethod.POST)
    public void batchChangeBackStatus(@PathVariable String ids, RestJsonWrapperBean wrapper) {
        getService().batchChangeBackStatus(ids,wrapper);
    }

}
