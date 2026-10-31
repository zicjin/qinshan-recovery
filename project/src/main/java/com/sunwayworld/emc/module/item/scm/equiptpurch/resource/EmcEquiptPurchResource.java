package com.sunwayworld.emc.module.item.scm.equiptpurch.resource;

import org.springframework.web.bind.annotation.RequestMapping;

import com.sunwayworld.emc.module.item.scm.equiptpurch.bean.EmcEquiptPurchBean;
import com.sunwayworld.emc.module.item.scm.equiptpurch.service.EmcEquiptPurchService;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.support.auditable.resource.GenericAuditableResource;
import com.sunwayworld.framework.support.base.resource.GenericResource;

@RequestMapping("/secure/emc/module/item/scm/equipt-purchs")
public interface EmcEquiptPurchResource extends GenericResource<EmcEquiptPurchService, EmcEquiptPurchBean, Long>,GenericAuditableResource<EmcEquiptPurchService, EmcEquiptPurchBean, Long>{
        
        Long insert(RestJsonWrapperBean wrapper);
        
}
