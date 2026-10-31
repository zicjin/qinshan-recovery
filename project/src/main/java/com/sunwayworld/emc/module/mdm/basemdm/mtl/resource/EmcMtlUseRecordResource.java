package com.sunwayworld.emc.module.mdm.basemdm.mtl.resource;


import org.springframework.web.bind.annotation.RequestMapping;

import com.sunwayworld.emc.module.mdm.basemdm.mtl.bean.EmcMtlUseRecordBean;
import com.sunwayworld.emc.module.mdm.basemdm.mtl.service.EmcMtlUseRecordService;
import com.sunwayworld.emc.module.mdm.basemdm.mtl.bean.EmcStockLogBean;
import com.sunwayworld.framework.data.page.Page;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.support.base.resource.GenericResource;
import com.sunwayworld.framework.support.choosable.resource.GenericChoosableResource;

@RequestMapping("/secure/emc/module/mdm/basemdm/mtl-use-records")
public interface EmcMtlUseRecordResource extends GenericResource<EmcMtlUseRecordService, EmcMtlUseRecordBean, Long>, 
                                                 GenericChoosableResource<EmcMtlUseRecordService, EmcMtlUseRecordBean, Long> {
       
	Page<EmcStockLogBean> selectStockLogInApply(RestJsonWrapperBean wrapper);
	
	Page<EmcMtlUseRecordBean> selectMtlUseRecordPagationByApplyId(String id, RestJsonWrapperBean wrapper);

	void apply(Long id, RestJsonWrapperBean wrapper);
}
