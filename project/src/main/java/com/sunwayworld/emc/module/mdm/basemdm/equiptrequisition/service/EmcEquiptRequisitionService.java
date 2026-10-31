package com.sunwayworld.emc.module.mdm.basemdm.equiptrequisition.service;

import com.sunwayworld.emc.module.mdm.basemdm.equiptrequisition.bean.EmcEquiptRequisitionBean;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.support.base.service.GenericService;

public interface EmcEquiptRequisitionService extends GenericService<EmcEquiptRequisitionBean, Long> {

    void confirmBack(String ids,RestJsonWrapperBean wrapper);

    void batchChangeRentStatus(String ids, RestJsonWrapperBean wrapper);

    void batchChangeBackStatus(String ids, RestJsonWrapperBean wrapper);
}
