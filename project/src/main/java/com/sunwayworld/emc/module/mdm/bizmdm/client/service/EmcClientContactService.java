package com.sunwayworld.emc.module.mdm.bizmdm.client.service;

import com.sunwayworld.emc.module.mdm.bizmdm.client.bean.EmcClientContactBean;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.support.base.service.GenericService;
import com.sunwayworld.framework.support.choosable.service.GenericChoosableService;

/**
 * @author magh@sunwayworld.com
 * @date 2019-10-19
 */

public interface EmcClientContactService extends GenericService<EmcClientContactBean, Long>, GenericChoosableService<EmcClientContactBean, Long> {
    void insert(Long id, RestJsonWrapperBean wrapper);
}
