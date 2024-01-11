package com.sunwayworld.emc.module.item.mtlpercheck.resource;

import org.springframework.web.bind.annotation.RequestMapping;

import com.sunwayworld.emc.module.item.mtlpercheck.bean.EmcMtlPerCheckBean;
import com.sunwayworld.emc.module.item.mtlpercheck.service.EmcMtlPerCheckService;
import com.sunwayworld.framework.support.base.resource.GenericResource;

@RequestMapping("/secure/emc/module/item/mtl-per-checks")
public interface EmcMtlPerCheckResource extends GenericResource<EmcMtlPerCheckService, EmcMtlPerCheckBean, Long> {
}
