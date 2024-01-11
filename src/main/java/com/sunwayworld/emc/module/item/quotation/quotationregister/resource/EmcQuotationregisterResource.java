package com.sunwayworld.emc.module.item.quotation.quotationregister.resource;

import org.springframework.web.bind.annotation.RequestMapping;

import com.sunwayworld.emc.module.item.quotation.quotationregister.bean.EmcQuotationregisterBean;
import com.sunwayworld.emc.module.item.quotation.quotationregister.service.EmcQuotationregisterService;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.support.auditable.resource.GenericAuditableResource;
import com.sunwayworld.framework.support.base.resource.GenericResource;

@RequestMapping("/secure/emc/module/item/quotation/quotationregisters")
public interface EmcQuotationregisterResource extends GenericResource<EmcQuotationregisterService, EmcQuotationregisterBean, Long>, GenericAuditableResource<EmcQuotationregisterService, EmcQuotationregisterBean, Long> {
    void addDiscount(RestJsonWrapperBean wrapper);

    void calculation(RestJsonWrapperBean wrapper);

    void addContract(RestJsonWrapperBean wrapper);
}
