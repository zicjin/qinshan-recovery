package com.sunwayworld.emc.module.mdm.basemdm.mtl.service;

import com.sunwayworld.emc.module.mdm.basemdm.mtl.bean.EmcMtlMaintainBean;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.support.base.service.GenericService;
/**
 * @author baogx@sunwayworld.com
 *
 * @date 2019年9月21日 下午3:40:30
 */
public interface EmcMtlMaintainService extends GenericService<EmcMtlMaintainBean, Long> {

    Long insertMaintain(String id, RestJsonWrapperBean wrapper);
    
}
