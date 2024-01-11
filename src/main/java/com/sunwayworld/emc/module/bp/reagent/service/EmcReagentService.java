package com.sunwayworld.emc.module.bp.reagent.service;

import com.sunwayworld.emc.module.bp.reagent.bean.EmcReagentBean;
import com.sunwayworld.emc.module.bp.reagent.bean.EmcReagentCompositionBean;
import com.sunwayworld.framework.data.page.Page;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.support.base.service.GenericService;


public interface EmcReagentService extends GenericService<EmcReagentBean, Long> {

    Page<EmcReagentCompositionBean> selectReagentCompositionPagination(Long id, RestJsonWrapperBean wrapper);

    Long insertComposition(Long id, RestJsonWrapperBean wrapper);

    void deleteComposition(Long id, RestJsonWrapperBean wrapper);

    Long copyReagent(Long id, RestJsonWrapperBean wrapper);
}
