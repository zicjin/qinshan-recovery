package com.sunwayworld.emc.module.mdm.purchapply.service;

import com.sunwayworld.emc.module.mdm.purchapply.bean.EmcPurchDetailBean;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.support.base.service.GenericService;
import com.sunwayworld.framework.support.choosable.service.GenericChoosableService;

/**
 * @author zhanghy@sunwayworld.com 2020-09-03
 */
public interface EmcPurchDetailService extends GenericService<EmcPurchDetailBean, Long>,
                                                  GenericChoosableService<EmcPurchDetailBean, Long>{

	Long insertPurchDetail(Long id, RestJsonWrapperBean wrapper);

	void deletePurchDetail(RestJsonWrapperBean wrapper);

}
