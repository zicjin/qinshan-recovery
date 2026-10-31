package com.sunwayworld.emc.module.mdm.basemdm.equiptTemp.resource;


import org.springframework.web.bind.annotation.RequestMapping;

import com.sunwayworld.emc.module.mdm.basemdm.equiptTemp.bean.PfEquiptTempBean;
import com.sunwayworld.emc.module.mdm.basemdm.equiptTemp.service.PfEquiptTempService;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.support.base.resource.GenericResource;
import com.sunwayworld.framework.support.choosable.resource.GenericChoosableResource;

@RequestMapping("/secure/emc/module/mdm/basemdm/equipt-temp")
public interface PfEquiptTempResource extends GenericResource<PfEquiptTempService, PfEquiptTempBean, Long>, GenericChoosableResource<PfEquiptTempService, PfEquiptTempBean, Long> {

    void batchInsertTemp(RestJsonWrapperBean wrapper);

}
