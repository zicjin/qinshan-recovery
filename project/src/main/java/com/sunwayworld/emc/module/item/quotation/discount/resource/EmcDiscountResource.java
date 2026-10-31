package com.sunwayworld.emc.module.item.quotation.discount.resource;

import org.springframework.web.bind.annotation.RequestMapping;

import com.sunwayworld.emc.module.item.quotation.discount.bean.EmcDiscountBean;
import com.sunwayworld.emc.module.item.quotation.discount.service.EmcDiscountService;
import com.sunwayworld.framework.support.base.resource.GenericResource;
import com.sunwayworld.framework.support.choosable.resource.GenericChoosableResource;

@RequestMapping("/secure/emc/module/item/quotation/discounts")
public interface EmcDiscountResource extends GenericResource<EmcDiscountService, EmcDiscountBean, Long>, GenericChoosableResource<EmcDiscountService, EmcDiscountBean, Long> {
}
