package com.sunwayworld.emc.module.mdm.bizmdm.test.service;

import com.sunwayworld.emc.module.mdm.bizmdm.test.bean.EmcTestMethodOrderCategoryBean;
import com.sunwayworld.framework.data.page.Page;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.support.base.service.GenericService;

/**
 * @author yuanhh@sunwayworld.com
 * @date 2019-09-19
 */

public interface EmcTestMethodOrderCategoryService extends GenericService<EmcTestMethodOrderCategoryBean, Long> {
    Long insert(Long testMethodId, RestJsonWrapperBean wrapper);
    
    Page<EmcTestMethodOrderCategoryBean> selectMaintenancePage(RestJsonWrapperBean wrapper);

}
