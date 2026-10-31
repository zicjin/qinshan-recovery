package com.sunwayworld.emc.module.mdm.equipt.equiptcheck.service;

import com.sunwayworld.emc.module.mdm.equipt.equiptcheck.bean.EmcEquipDataBean;
import com.sunwayworld.framework.data.page.Page;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.support.base.service.GenericService;

public interface EmcEquipDataService extends GenericService<EmcEquipDataBean, Long> {

    Page<EmcEquipDataBean> selectEquiptDataPaginationById(Long id, RestJsonWrapperBean wrapper);

    Long insertEquiptData(Long id, RestJsonWrapperBean wrapper);
}
