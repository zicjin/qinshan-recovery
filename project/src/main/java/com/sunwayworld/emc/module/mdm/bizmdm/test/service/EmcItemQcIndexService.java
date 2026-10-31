package com.sunwayworld.emc.module.mdm.bizmdm.test.service;

import com.sunwayworld.emc.module.mdm.bizmdm.test.bean.EmcItemQcIndexBean;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.support.base.service.GenericService;

/**
 * @author yuanhh@sunwayworld.com
 * @date 2019-09-19
 */

public interface EmcItemQcIndexService extends GenericService<EmcItemQcIndexBean, Long> {
    Long insert(Long itemId, RestJsonWrapperBean wrapper);

    void copyTo(RestJsonWrapperBean wrapper);

}
