package com.sunwayworld.emc.module.mdm.bizmdm.capacity.service;

import com.sunwayworld.emc.module.mdm.bizmdm.capacity.bean.EmcCapacityItemGroupBean;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.support.activatable.service.GenericActivatableService;
import com.sunwayworld.framework.support.base.service.GenericService;
import com.sunwayworld.framework.support.choosable.service.GenericChoosableService;

/**
 * @author luoc@sunwayworld.com
 * @date 2021-04-15
 */

public interface EmcCapacityItemGroupService extends GenericService<EmcCapacityItemGroupBean, Long>, GenericActivatableService<EmcCapacityItemGroupBean, Long>, GenericChoosableService<EmcCapacityItemGroupBean, Long> {
    Long batchInsert(Long id,RestJsonWrapperBean jsonWrapper);
}
