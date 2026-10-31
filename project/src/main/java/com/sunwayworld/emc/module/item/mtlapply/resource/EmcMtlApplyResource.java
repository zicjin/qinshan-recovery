package com.sunwayworld.emc.module.item.mtlapply.resource;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.sunwayworld.emc.module.item.mtlapply.bean.EmcMtlApplyBean;
import com.sunwayworld.emc.module.item.mtlapply.service.EmcMtlApplyService;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.restful.data.RestValidationResultBean;
import com.sunwayworld.framework.support.auditable.resource.GenericAuditableResource;
import com.sunwayworld.framework.support.base.resource.GenericResource;

/**
 * @author zhengxy@sunwayworld.com 2020年02月24日
 */
@RequestMapping("/secure/emc/module/item/mtl-applies")
public interface EmcMtlApplyResource extends GenericResource<EmcMtlApplyService, EmcMtlApplyBean, Long>,
                                             GenericAuditableResource<EmcMtlApplyService, EmcMtlApplyBean, Long> {

    @RequestMapping(value = "/action/validate-stock", method = RequestMethod.POST)
    RestValidationResultBean validateStock(RestJsonWrapperBean wrapper);
}
