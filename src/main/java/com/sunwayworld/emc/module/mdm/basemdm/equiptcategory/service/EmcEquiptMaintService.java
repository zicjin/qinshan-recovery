package com.sunwayworld.emc.module.mdm.basemdm.equiptcategory.service;

import com.sunwayworld.emc.module.mdm.basemdm.equiptcategory.bean.EmcEquiptMaintBean;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.support.base.service.GenericService;

public interface EmcEquiptMaintService extends GenericService<EmcEquiptMaintBean, Long> {

    Long insert(Long id, RestJsonWrapperBean wrapper);
}
