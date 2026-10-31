package com.sunwayworld.emc.module.mdm.basemdm.mtl.resource;

import org.springframework.web.bind.annotation.RequestMapping;

import com.sunwayworld.emc.module.mdm.basemdm.mtl.bean.EmcStockBean;
import com.sunwayworld.emc.module.mdm.basemdm.mtl.service.PfStockService;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.support.base.resource.GenericResource;


@RequestMapping("/secure/emc/module/mdm/basemdm/stock")
public interface PfStockResource extends GenericResource<PfStockService, EmcStockBean, Long>{

    void copy(RestJsonWrapperBean wrapper);
}
