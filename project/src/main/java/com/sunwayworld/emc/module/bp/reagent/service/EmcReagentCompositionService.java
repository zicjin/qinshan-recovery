package com.sunwayworld.emc.module.bp.reagent.service;

import com.sunwayworld.emc.module.bp.reagent.bean.EmcReagentCompositionBean;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.support.base.service.GenericService;

/**
* @author zhengxy@sunwayworld.com 2020年06月18日
*/
public interface EmcReagentCompositionService extends GenericService<EmcReagentCompositionBean, Long> {

    Long insert(Long reagentId, RestJsonWrapperBean wrapper);
}
