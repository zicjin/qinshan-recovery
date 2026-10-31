package com.sunwayworld.emc.module.mdm.basemdm.fetchdata.resource;

import org.springframework.web.bind.annotation.RequestMapping;

import com.sunwayworld.emc.module.mdm.basemdm.fetchdata.bean.EmcFetchDataBean;
import com.sunwayworld.emc.module.mdm.basemdm.fetchdata.service.EmcFetchDataService;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.support.activatable.resource.GenericActivatableResource;
import com.sunwayworld.framework.support.base.resource.GenericResource;

/**
 * @author zhengxy@sunwayworld.com 2020年03月05日
 */
@RequestMapping("/secure/emc/module/mdm/basemdm/fetch-datas")
public interface EmcFetchDataResource extends GenericResource<EmcFetchDataService, EmcFetchDataBean, Long>,
                                              GenericActivatableResource<EmcFetchDataService, EmcFetchDataBean, Long> {

    void generateLine(RestJsonWrapperBean wrapper);
}
