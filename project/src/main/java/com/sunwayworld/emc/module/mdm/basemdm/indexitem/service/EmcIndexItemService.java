package com.sunwayworld.emc.module.mdm.basemdm.indexitem.service;

import com.sunwayworld.emc.module.mdm.basemdm.indexitem.bean.EmcIndexItemBean;
import com.sunwayworld.emc.module.mdm.basemdm.indexitem.bean.EmcIndexTestItemBean;
import com.sunwayworld.framework.data.page.Page;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.support.base.service.GenericService;
import com.sunwayworld.framework.support.choosable.service.GenericChoosableService;

/**
 * @author magh@sunwayworld.com
 * @date 2019-09-17
 */

public interface EmcIndexItemService extends GenericService<EmcIndexItemBean, Long>, GenericChoosableService<EmcIndexItemBean, Long> {

    Page<EmcIndexTestItemBean> selectIndexTestItemLimit(RestJsonWrapperBean wrapper);

}
