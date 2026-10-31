package com.sunwayworld.emc.module.mdm.emp.emp.service;

import com.sunwayworld.emc.module.mdm.emp.emp.bean.EmcEmpQualBean;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.support.base.service.GenericService;

/**
 * @author yuanhh@sunwayworld.com 2019年9月4日
 */
public interface EmcEmpQualService extends GenericService<EmcEmpQualBean, Long> {

    Long insert(String empId, RestJsonWrapperBean wrapper);
}
