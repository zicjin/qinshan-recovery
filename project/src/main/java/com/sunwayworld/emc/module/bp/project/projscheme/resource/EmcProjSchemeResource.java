package com.sunwayworld.emc.module.bp.project.projscheme.resource;

import org.springframework.web.bind.annotation.RequestMapping;

import com.sunwayworld.emc.module.bp.project.projscheme.bean.EmcProjDTO;
import com.sunwayworld.emc.module.bp.project.projscheme.bean.EmcProjOrderBean;
import com.sunwayworld.emc.module.bp.project.projscheme.bean.EmcProjSchemeBean;
import com.sunwayworld.emc.module.bp.project.projscheme.service.EmcProjSchemeService;
import com.sunwayworld.framework.data.page.Page;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.support.base.resource.GenericResource;

/**
 * @author yuanhh@sunwayworld.com
 * @date 2019-10-08
 */
@RequestMapping("/secure/emc/module/bp/project/proj-schemes")
public interface EmcProjSchemeResource extends GenericResource<EmcProjSchemeService, EmcProjSchemeBean, Long> {

    Page<EmcProjOrderBean> selectProjOrderPagationByProjScheme(Long id, RestJsonWrapperBean wrapper);

    Page<EmcProjOrderBean> selectOrderPagation(RestJsonWrapperBean wrapper);

    Page<EmcProjDTO> selectPagationByProjId(Long id, RestJsonWrapperBean wrapper);

    void copy(RestJsonWrapperBean wrapper);

    void copyPoint(RestJsonWrapperBean wrapper);

    void updateFrequency(RestJsonWrapperBean wrapper);

    void updateProjOrderItemGroup(RestJsonWrapperBean wrapper);

    void editSubcontractor(RestJsonWrapperBean wrapper);

    void initInsert(RestJsonWrapperBean wrapper);

    void updateOrderIndex(RestJsonWrapperBean wrapper);

    void relevanceUpdateSubcontractor(RestJsonWrapperBean wrapper);

    void relevanceDeleteSubcontractor(RestJsonWrapperBean wrapper);

}
