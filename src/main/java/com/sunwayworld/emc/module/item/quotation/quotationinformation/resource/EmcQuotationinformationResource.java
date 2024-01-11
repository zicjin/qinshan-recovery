package com.sunwayworld.emc.module.item.quotation.quotationinformation.resource;

import org.springframework.web.bind.annotation.RequestMapping;

import com.sunwayworld.emc.module.item.quotation.quotationinformation.bean.EmcQuotationinformationBean;
import com.sunwayworld.emc.module.item.quotation.quotationinformation.service.EmcQuotationinformationService;
import com.sunwayworld.framework.support.base.resource.GenericResource;

@RequestMapping("/secure/emc/module/item/quotation/quotationinformations")
public interface EmcQuotationinformationResource extends GenericResource<EmcQuotationinformationService, EmcQuotationinformationBean, Long> {
}
