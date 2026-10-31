package com.sunwayworld.emc.module.bp.project.projectqc.resource;

import java.util.List;

import org.springframework.web.bind.annotation.RequestMapping;

import com.sunwayworld.emc.module.bp.project.projectqc.bean.EmcProjectQcBean;
import com.sunwayworld.emc.module.bp.project.projectqc.bean.EmcProjectQcLineBean;
import com.sunwayworld.emc.module.bp.project.projectqc.service.EmcProjectQcService;
import com.sunwayworld.framework.data.page.Page;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.support.base.resource.GenericResource;
import com.sunwayworld.framework.support.table.bean.TableHeadBean;
import com.sunwayworld.framework.support.table.bean.TableRowBean;

/**
* @author zhengxy@sunwayworld.com 2020年09月21日
*/
@RequestMapping("/secure/emc/module/bp/project/project-qcs")
public interface EmcProjectQcResource extends GenericResource<EmcProjectQcService, EmcProjectQcBean, Long> {

    Page<EmcProjectQcBean> selectOrderCategoryPagination(Long id, RestJsonWrapperBean wrapper);

    Page<EmcProjectQcLineBean> selectQcLinePagination(Long id, String monitorTmplId, RestJsonWrapperBean wrapper);

    List<TableHeadBean> selectQcLineTitleList(RestJsonWrapperBean wrapper);

    List<TableRowBean> selectQcLineRowList(RestJsonWrapperBean wrapper);
}
