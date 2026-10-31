package com.sunwayworld.emc.module.item.quotation.addprice.resource;

import org.springframework.web.bind.annotation.RequestMapping;

import com.sunwayworld.emc.module.item.quotation.addprice.bean.EmcAddpriceBean;
import com.sunwayworld.emc.module.item.quotation.addprice.service.EmcAddpriceService;
import com.sunwayworld.framework.support.base.resource.GenericResource;
import com.sunwayworld.framework.support.choosable.resource.GenericChoosableResource;

@RequestMapping("/secure/emc/module/item/quotation/addprices")
public interface EmcAddpriceResource extends GenericResource<EmcAddpriceService, EmcAddpriceBean, Long>, GenericChoosableResource<EmcAddpriceService, EmcAddpriceBean, Long> {
}
