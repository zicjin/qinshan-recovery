package com.sunwayworld.emc.module.bp.reagent.resource;

import org.springframework.web.bind.annotation.RequestMapping;

import com.sunwayworld.emc.module.bp.reagent.bean.EmcReagentBean;
import com.sunwayworld.emc.module.bp.reagent.bean.EmcReagentCompositionBean;
import com.sunwayworld.emc.module.bp.reagent.service.EmcReagentService;
import com.sunwayworld.framework.data.page.Page;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.support.base.resource.GenericResource;

@RequestMapping("/secure/emc/module/bp/reagents")
public interface EmcReagentResource extends GenericResource<EmcReagentService, EmcReagentBean, Long> {

    Page<EmcReagentCompositionBean> selectReagentCompositionPagination(Long id, RestJsonWrapperBean wrapper);

    Long insertComposition(Long id, RestJsonWrapperBean wrapper);

    void deleteComposition(Long id, RestJsonWrapperBean wrapper);

    Long copyReagent(long id,RestJsonWrapperBean wrapperBean);
}
