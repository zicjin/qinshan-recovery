package com.sunwayworld.emc.module.mdm.user.service;

import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.module.mdm.user.service.CoreUserService;
/**
 * @author licm@sunwayworld.com 2021年09月10日
 */

public interface PfUserService extends CoreUserService {
	
    String selectMaxId(RestJsonWrapperBean wrapper);
    
    void insertReportConfigByDept(RestJsonWrapperBean wrapper);  
    
    void deleteReportConfigByDept(RestJsonWrapperBean wrapper);  
    
}
