package com.sunwayworld.emc.module.bp.project.projectqc.service;

import java.util.List;

import com.sunwayworld.emc.module.bp.project.project.bean.EmcProjectBean;
import com.sunwayworld.emc.module.bp.project.projectqc.bean.EmcProjectQcBean;
import com.sunwayworld.framework.data.page.Page;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.support.base.service.GenericService;
import com.sunwayworld.framework.support.table.bean.TableHeadBean;
import com.sunwayworld.framework.support.table.bean.TableRowBean;

/**
* @author zhengxy@sunwayworld.com 2020年09月21日
*/
public interface EmcProjectQcService extends GenericService<EmcProjectQcBean, Long> {

    void insert(List<EmcProjectBean> projectList);

    void delete(List<Long> projectList);

    Page<EmcProjectQcBean> selectOrderCategoryPagination(Long id, RestJsonWrapperBean wrapper);

    List<TableHeadBean> selectQcLineTitleList(RestJsonWrapperBean wrapper);

    List<TableRowBean> selectQcLineRowList(RestJsonWrapperBean wrapper);
}
