package com.sunwayworld.emc.module.mdm.bizmdm.test.service;

import com.sunwayworld.emc.module.mdm.bizmdm.test.bean.EmcItemRevisionBean;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.support.base.service.GenericService;

/**
 * @author yuanhh@sunwayworld.com
 * @date 2019-09-19
 */

public interface EmcItemRevisionService extends GenericService<EmcItemRevisionBean, Long> {
    Long insert(Long itemId, RestJsonWrapperBean wrapper);

    void copyItemRevision(RestJsonWrapperBean wrapper);

    void copyItemRevisionTo(RestJsonWrapperBean wrapper);
}
