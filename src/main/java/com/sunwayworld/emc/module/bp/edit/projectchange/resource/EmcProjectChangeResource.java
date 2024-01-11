package com.sunwayworld.emc.module.bp.edit.projectchange.resource;

import org.springframework.web.bind.annotation.RequestMapping;

import com.sunwayworld.emc.module.bp.edit.projectchange.bean.EmcOrderChangeBean;
import com.sunwayworld.emc.module.bp.edit.projectchange.bean.EmcProjectChangeBean;
import com.sunwayworld.emc.module.bp.edit.projectchange.service.EmcProjectChangeService;
import com.sunwayworld.emc.module.bp.project.project.bean.EmcProjectDirectorBean;
import com.sunwayworld.framework.data.page.Page;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.support.auditable.resource.GenericAuditableResource;
import com.sunwayworld.framework.support.base.resource.GenericResource;

/**
 * @author magh@sunwayworld.com
 * @date 2019-09-27
 */
@RequestMapping("/secure/emc/module/bp/edit/project-change/projects")
public interface EmcProjectChangeResource extends GenericResource<EmcProjectChangeService, EmcProjectChangeBean, Long>, GenericAuditableResource<EmcProjectChangeService, EmcProjectChangeBean, Long> {

    Page<EmcOrderChangeBean> selectOrderPaginationByProjectId(Long id, RestJsonWrapperBean wrapper);

    Long insertOrder(Long id, RestJsonWrapperBean wrapper);

    void deleteOrder(Long id, RestJsonWrapperBean wrapper);

    void copeOrder(RestJsonWrapperBean jsonWrapper);

    void copeQcOrder(RestJsonWrapperBean jsonWrapper);

    Long selectIdByKeyword(String keyword, RestJsonWrapperBean wrapper);

    Page<EmcProjectDirectorBean> selectDirectorPaginationByProjectId(Long id, RestJsonWrapperBean wrapper);

    void insertDirector(RestJsonWrapperBean wrapper);

    void deleteDirector(RestJsonWrapperBean wrapper);

    EmcProjectChangeBean selectProjectDetail(Long id, RestJsonWrapperBean wrapper);
}
