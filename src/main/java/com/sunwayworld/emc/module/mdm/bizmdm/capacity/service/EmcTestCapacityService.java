package com.sunwayworld.emc.module.mdm.bizmdm.capacity.service;

import com.sunwayworld.emc.module.mdm.bizmdm.capacity.bean.EmcTestCapacityBean;
import com.sunwayworld.emc.module.mdm.bizmdm.monitortmpl.bean.EmcMonitorSchemeBean;
import com.sunwayworld.framework.data.page.Page;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.support.base.service.GenericService;

public interface EmcTestCapacityService extends GenericService<EmcTestCapacityBean, Long> {

    Long batchInsert(RestJsonWrapperBean jsonWrapper);

    Page<EmcMonitorSchemeBean> selectSchemeChoosablePagination(RestJsonWrapperBean wrapper);

}
