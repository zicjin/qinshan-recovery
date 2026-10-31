package com.sunwayworld.emc.module.mdm.basemdm.indexitem.service;

import com.sunwayworld.emc.module.mdm.basemdm.indexitem.bean.EmcIndexTestItemBean;
import com.sunwayworld.framework.data.page.Page;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.support.base.service.GenericService;

/**
 * @author magh@sunwayworld.com
 * @date 2019-09-17
 */

public interface EmcIndexTestItemService extends GenericService<EmcIndexTestItemBean, Long> {

    Long insert(Long indexItemId, RestJsonWrapperBean wrapper);
    
    Page<EmcIndexTestItemBean> selectIndexTestItemLimit(RestJsonWrapperBean wrapper);
}
