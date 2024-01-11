package com.sunwayworld.emc.module.mdm.basemdm.mtl.service;

import com.sunwayworld.emc.module.mdm.basemdm.mtl.bean.EmcMtlUseRecordBean;
import com.sunwayworld.emc.module.mdm.basemdm.mtl.bean.EmcStockLogBean;
import com.sunwayworld.framework.data.page.Page;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.support.base.service.GenericService;
import com.sunwayworld.framework.support.choosable.service.GenericChoosableService;

public interface EmcMtlUseRecordService extends GenericService<EmcMtlUseRecordBean, Long>, GenericChoosableService<EmcMtlUseRecordBean, Long> {

	Page<EmcStockLogBean> selectStockLogInApply(RestJsonWrapperBean wrapper);

	void apply(Long id, RestJsonWrapperBean wrapper);

}
