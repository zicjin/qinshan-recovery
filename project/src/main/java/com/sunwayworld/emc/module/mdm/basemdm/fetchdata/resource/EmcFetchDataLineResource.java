package com.sunwayworld.emc.module.mdm.basemdm.fetchdata.resource;

import org.springframework.web.bind.annotation.RequestMapping;

import com.sunwayworld.emc.module.mdm.basemdm.fetchdata.bean.EmcFetchDataLineBean;
import com.sunwayworld.emc.module.mdm.basemdm.fetchdata.service.EmcFetchDataLineService;
import com.sunwayworld.framework.support.base.resource.GenericResource;

/**
 * @author zhengxy@sunwayworld.com 2020年03月05日
 */
@RequestMapping("/secure/emc/module/mdm/basemdm/fetch-data-lines")
public interface EmcFetchDataLineResource extends GenericResource<EmcFetchDataLineService, EmcFetchDataLineBean, Long> {

}
