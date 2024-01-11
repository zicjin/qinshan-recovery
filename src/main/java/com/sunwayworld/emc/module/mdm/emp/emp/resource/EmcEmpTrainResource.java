package com.sunwayworld.emc.module.mdm.emp.emp.resource;


import com.sunwayworld.emc.module.mdm.emp.emp.bean.EmcEmpTrainBean;
import com.sunwayworld.framework.data.page.Page;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;


public interface EmcEmpTrainResource extends EmcEmpResource {

    void insertTrain(String empId, RestJsonWrapperBean wrapper);

    Page<EmcEmpTrainBean> selectTrainPagationByEmp(String id, RestJsonWrapperBean wrapper);

    void deleteTrain(String id, RestJsonWrapperBean wrapper);
}
