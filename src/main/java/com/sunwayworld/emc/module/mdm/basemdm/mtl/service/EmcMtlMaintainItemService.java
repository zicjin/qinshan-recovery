package com.sunwayworld.emc.module.mdm.basemdm.mtl.service;

import com.sunwayworld.emc.module.mdm.basemdm.mtl.bean.EmcMtlMaintainItemBean;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.support.base.service.GenericService;
/**
 * @author baogx@sunwayworld.com
 *
 * @date 2019年9月21日 下午3:43:45
 */
public interface EmcMtlMaintainItemService extends GenericService<EmcMtlMaintainItemBean, Long> {

    Long insert(Long maintainId, RestJsonWrapperBean wrapper);
}
