package com.sunwayworld.emc.module.mdm.bizmdm.indexcategory.service;

import com.sunwayworld.emc.module.mdm.bizmdm.indexcategory.bean.EmcOrderIndexBean;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.support.activatable.service.GenericActivatableService;
import com.sunwayworld.framework.support.base.service.GenericService;
import com.sunwayworld.framework.support.choosable.service.GenericChoosableService;

/**
 * @author zhangsy@sunwayworld.com 2019年9月17日
 */

public interface EmcOrderIndexService extends GenericService<EmcOrderIndexBean, String>, GenericActivatableService<EmcOrderIndexBean, String>, GenericChoosableService<EmcOrderIndexBean, String> {

    String insert(Long id, RestJsonWrapperBean wrapper);

    void copy(RestJsonWrapperBean jsonWrapper);
}
