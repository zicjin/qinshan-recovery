package com.sunwayworld.emc.module.mdm.purchapply.resource;

import org.springframework.web.bind.annotation.RequestMapping;

import com.sunwayworld.emc.module.mdm.purchapply.bean.EmcPurchApplyBean;
import com.sunwayworld.emc.module.mdm.purchapply.bean.EmcPurchDetailBean;
import com.sunwayworld.emc.module.mdm.purchapply.service.EmcPurchApplyService;
import com.sunwayworld.framework.data.page.Page;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.support.auditable.resource.GenericAuditableResource;
import com.sunwayworld.framework.support.base.resource.GenericResource;
import com.sunwayworld.framework.support.choosable.resource.GenericChoosableResource;

/**
 * @author zhanghy@sunwayworld.com 2020-09-03
 */
@RequestMapping("/secure/emc/module/mdm/purch-applies")
public interface EmcPurchApplyResource extends GenericResource<EmcPurchApplyService, EmcPurchApplyBean, Long>, GenericAuditableResource<EmcPurchApplyService, EmcPurchApplyBean, Long> ,GenericChoosableResource<EmcPurchApplyService, EmcPurchApplyBean, Long>{

	Page<EmcPurchDetailBean> selectPurchDetail(String id, RestJsonWrapperBean wrapper);

	Long insertPurchDetail(Long id, RestJsonWrapperBean wrapper);

	void deletePurchDetail(RestJsonWrapperBean wrapper);
}
