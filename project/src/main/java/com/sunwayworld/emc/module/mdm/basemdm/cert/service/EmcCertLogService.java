package com.sunwayworld.emc.module.mdm.basemdm.cert.service;

import com.sunwayworld.emc.module.mdm.basemdm.cert.bean.EmcCertLogBean;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.support.base.service.GenericService;

/**
 * @author zhangsy@sunwayworld.com
 * @date 2019-10-08
 */

public interface EmcCertLogService extends GenericService<EmcCertLogBean, Long> {
    Long insert(RestJsonWrapperBean wrapper);
}
