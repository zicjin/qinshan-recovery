package com.sunwayworld.emc.module.mdm.basemdm.monitorpointcategory.service;

import com.sunwayworld.emc.module.mdm.basemdm.monitorpointcategory.bean.EmcMpcTmplBean;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.support.base.service.GenericService;
import com.sunwayworld.framework.support.selectable.service.GenericSelectableService;

/**
 * @author magh@sunwayworld.com
 * @date 2019-09-10
 */

public interface EmcMpcTmplService extends GenericService<EmcMpcTmplBean, Long>,GenericSelectableService<EmcMpcTmplBean, Long> {

    Long insert(String monitorPointCategoryId, RestJsonWrapperBean wrapper);
}
