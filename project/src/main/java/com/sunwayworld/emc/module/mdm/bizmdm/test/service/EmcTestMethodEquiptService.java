package com.sunwayworld.emc.module.mdm.bizmdm.test.service;

import com.sunwayworld.emc.module.mdm.bizmdm.test.bean.EmcTestMethodEquiptBean;
import com.sunwayworld.emc.module.mdm.equipt.equipt.bean.EmcEquiptTestMethodBean;
import com.sunwayworld.framework.data.page.Page;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.support.base.service.GenericService;

/**
 * @author yuanhh@sunwayworld.com
 * @date 2019-09-19
 */

public interface EmcTestMethodEquiptService extends GenericService<EmcTestMethodEquiptBean, Long> {
    void insert(Long testMethodId, RestJsonWrapperBean wrapper);
    
    void deleteEquipt(RestJsonWrapperBean wrapper);
    
    void setDefault(Long testMethodId, RestJsonWrapperBean wrapper);
    
    void copyTo(RestJsonWrapperBean jsonWrapper);
    
    Page<EmcEquiptTestMethodBean> selectEquiptPagationByTestMethod(Long id, RestJsonWrapperBean wrapper);
}
