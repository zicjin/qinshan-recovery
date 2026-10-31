package com.sunwayworld.emc.module.mdm.emp.emp.service;

import com.sunwayworld.emc.module.mdm.emp.emp.bean.EmcEmpTrainBean;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.support.base.service.GenericService;

public interface EmcEmpTrainService  extends GenericService<EmcEmpTrainBean, Long>{

    void insertTrain(String empId, RestJsonWrapperBean wrapper);

}
