package com.sunwayworld.emc.module.bp.edit.projectchange.resource;

import org.springframework.web.bind.annotation.RequestMapping;

import com.sunwayworld.emc.module.bp.edit.projectchange.bean.EmcProjOrderChangeBean;
import com.sunwayworld.emc.module.bp.edit.projectchange.bean.EmcProjSchemeChangeBean;
import com.sunwayworld.emc.module.bp.edit.projectchange.service.EmcProjSchemeChangeService;
import com.sunwayworld.emc.module.bp.project.projscheme.bean.EmcProjDTO;
import com.sunwayworld.framework.data.page.Page;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.support.base.resource.GenericResource;

/**
 * @author yuanhh@sunwayworld.com
 * @date 2019-10-08
 */
@RequestMapping("/secure/emc/module/bp/edit/project-change/proj-schemes")
public interface EmcProjSchemeChangeResource extends GenericResource<EmcProjSchemeChangeService, EmcProjSchemeChangeBean, Long> {

    Page<EmcProjOrderChangeBean> selectProjOrderPagationByProjScheme(Long id, RestJsonWrapperBean wrapper);

    Page<EmcProjOrderChangeBean> selectOrderPagation(RestJsonWrapperBean wrapper);

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
