package com.sunwayworld.emc.module.mdm.basemdm.fetchdata.service;

import com.sunwayworld.emc.module.mdm.basemdm.fetchdata.bean.EmcFetchDataBean;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.support.activatable.service.GenericActivatableService;
import com.sunwayworld.framework.support.base.service.GenericService;

/**
 * @author zhengxy@sunwayworld.com 2020年03月05日
 */
public interface EmcFetchDataService extends GenericService<EmcFetchDataBean, Long>,
                                             GenericActivatableService<EmcFetchDataBean, Long> {

    void generateLine(RestJsonWrapperBean wrapper);
}
