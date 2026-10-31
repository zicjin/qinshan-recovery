package com.sunwayworld.emc.module.mdm.bizmdm.method.service;

import com.sunwayworld.emc.module.mdm.bizmdm.method.bean.EmcMethodVersionBean;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.support.base.service.GenericService;

/**
 * @author yuanhh@sunwayworld.com 2019年9月10日
 */

public interface EmcMethodVersionService extends GenericService<EmcMethodVersionBean, Long> {
    Long insert(Long methodId, RestJsonWrapperBean wrapper);

    void deleteVersion(Long methodId, RestJsonWrapperBean wrapper);
}
