package com.sunwayworld.emc.module.mdm.bizmdm.test.service;

import com.sunwayworld.emc.module.mdm.bizmdm.test.bean.EmcTestMethodDefaultQcBean;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.support.base.service.GenericService;

/**
* @author zhengxy@sunwayworld.com 2021年06月17日
*/
public interface EmcTestMethodDefaultQcService extends GenericService<EmcTestMethodDefaultQcBean, Long> {

    void insert(Long id, RestJsonWrapperBean wrapper);
}
