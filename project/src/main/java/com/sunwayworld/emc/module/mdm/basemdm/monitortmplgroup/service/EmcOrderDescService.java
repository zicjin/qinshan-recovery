package com.sunwayworld.emc.module.mdm.basemdm.monitortmplgroup.service;

import com.sunwayworld.emc.module.mdm.basemdm.monitortmplgroup.bean.EmcOrderDescBean;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.support.activatable.service.GenericActivatableService;
import com.sunwayworld.framework.support.base.service.GenericService;

/**
 * @author magh@sunwayworld.com 2019年9月6日
 */

public interface EmcOrderDescService extends GenericService<EmcOrderDescBean, Long>, GenericActivatableService<EmcOrderDescBean, Long> {

    Long insert(Long monitorTmplGroupId, RestJsonWrapperBean wrapper);
}
