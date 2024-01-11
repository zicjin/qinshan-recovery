package com.sunwayworld.emc.module.mdm.bizmdm.test.service;

import java.util.List;

import com.sunwayworld.emc.module.mdm.bizmdm.test.bean.EmcTestBean;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.support.base.service.GenericService;
import com.sunwayworld.framework.support.choosable.service.GenericChoosableService;
import com.sunwayworld.framework.support.selectable.service.GenericSelectableService;

/**
 * @author magh@sunwayworld.com
 * @date 2019-10-18
 */

public interface EmcTestService extends GenericService<EmcTestBean, Long>, GenericChoosableService<EmcTestBean, Long>, GenericSelectableService<EmcTestBean, Long> {

    List<Long> checkUsed(RestJsonWrapperBean wrapper);
}
