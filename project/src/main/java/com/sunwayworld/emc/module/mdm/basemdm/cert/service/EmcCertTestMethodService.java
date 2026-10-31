package com.sunwayworld.emc.module.mdm.basemdm.cert.service;

import com.sunwayworld.emc.module.mdm.basemdm.cert.bean.EmcCertTestMethodBean;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.support.base.service.GenericService;

/**
 * @author zhangsy@sunwayworld.com
 * @date 2019-10-08
 */

public interface EmcCertTestMethodService extends GenericService<EmcCertTestMethodBean, Long> {
    
    void insert(Long id, RestJsonWrapperBean wrapper);

    void copyCertTestMethod(RestJsonWrapperBean wrapper);
    
}
