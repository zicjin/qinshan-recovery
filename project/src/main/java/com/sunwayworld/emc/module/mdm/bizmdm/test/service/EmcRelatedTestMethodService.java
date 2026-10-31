package com.sunwayworld.emc.module.mdm.bizmdm.test.service;

import com.sunwayworld.emc.module.mdm.bizmdm.test.bean.EmcRelatedTestMethodBean;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.support.base.service.GenericService;

/**
* @author zhengxy@sunwayworld.com 2021年08月26日
*/
public interface EmcRelatedTestMethodService extends GenericService<EmcRelatedTestMethodBean, Long> {

    void insert(Long testMethodId, RestJsonWrapperBean wrapper);
}
