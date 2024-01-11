package com.sunwayworld.emc.module.mdm.basemdm.equiptrequisition.resource;

import org.springframework.web.bind.annotation.RequestMapping;

import com.sunwayworld.emc.module.mdm.basemdm.equiptrequisition.bean.EmcEquiptRequisitionBean;
import com.sunwayworld.emc.module.mdm.basemdm.equiptrequisition.service.EmcEquiptRequisitionService;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.support.base.resource.GenericResource;

@RequestMapping("/secure/emc/module/mdm/basemdm/equipt-requisitions")
public interface EmcEquiptRequisitionResource extends GenericResource<EmcEquiptRequisitionService, EmcEquiptRequisitionBean, Long> {
    void confirmBack(String ids,RestJsonWrapperBean wrapper);

    void batchChangeRentStatus(String ids,RestJsonWrapperBean wrapper);

    void batchChangeBackStatus(String ids,RestJsonWrapperBean wrapper);
}
