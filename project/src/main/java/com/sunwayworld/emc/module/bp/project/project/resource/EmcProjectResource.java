package com.sunwayworld.emc.module.bp.project.project.resource;

import org.springframework.web.bind.annotation.RequestMapping;

import com.sunwayworld.emc.module.bp.bp.bean.EmcOrderBean;
import com.sunwayworld.emc.module.bp.project.project.bean.EmcProjectBean;
import com.sunwayworld.emc.module.bp.project.project.bean.EmcProjectDirectorBean;
import com.sunwayworld.emc.module.bp.project.project.service.EmcProjectService;
import com.sunwayworld.framework.data.page.Page;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.support.auditable.resource.GenericAuditableResource;
import com.sunwayworld.framework.support.base.resource.GenericResource;
import com.sunwayworld.framework.support.choosable.resource.GenericChoosableResource;

/**
 * @author magh@sunwayworld.com
 * @date 2019-09-27
 */
@RequestMapping("/secure/emc/module/bp/project/projects")
public interface EmcProjectResource extends GenericResource<EmcProjectService, EmcProjectBean, Long>, GenericChoosableResource<EmcProjectService, EmcProjectBean, Long>,
        GenericAuditableResource<EmcProjectService, EmcProjectBean, Long> {

    void copy(RestJsonWrapperBean wrapper);

    Page<EmcOrderBean> selectOrderPaginationByProjectId(Long id, RestJsonWrapperBean wrapper);

    Long insertOrder(Long id, RestJsonWrapperBean wrapper);

    void deleteOrder(Long id, RestJsonWrapperBean wrapper);

    void copeOrder(RestJsonWrapperBean jsonWrapper);

    void copeQcOrder(RestJsonWrapperBean jsonWrapper);

    void submitToReport(RestJsonWrapperBean wrapper);

    Page<EmcProjectBean> selectProjectStatistics(RestJsonWrapperBean wrapper);

    Page<EmcProjectBean> selectAnnualProjectStatistics(RestJsonWrapperBean wrapper);

    Page<EmcProjectBean> selectAllProjects(RestJsonWrapperBean wrapper);

    Long selectIdByKeyword(String keyword, RestJsonWrapperBean wrapper);

    Long insertPlan(RestJsonWrapperBean wrapper);

    void changeSchedule(RestJsonWrapperBean wrapper);

    void copyPlan(RestJsonWrapperBean wrapper);

    void submitPlan(RestJsonWrapperBean wrapper);

    void stopSchedule(RestJsonWrapperBean wrapper);

    Page<EmcProjectDirectorBean> selectDirectorPaginationByProjectId(Long id, RestJsonWrapperBean wrapper);

    void insertDirector(RestJsonWrapperBean wrapper);

    void deleteDirector(RestJsonWrapperBean wrapper);

    Page<EmcProjectBean> selectAnalysisAuditProject(RestJsonWrapperBean wrapper);

    EmcProjectBean selectProjectDetail(Long id, RestJsonWrapperBean wrapper);
}
