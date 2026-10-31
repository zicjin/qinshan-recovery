package com.sunwayworld.emc.module.bp.bp.dao;

import java.util.List;
import java.util.Map;

import com.sunwayworld.emc.module.bp.bp.bean.EmcOrderTaskBean;
import com.sunwayworld.framework.mybatis.mapper.MapperParameter;
import com.sunwayworld.framework.support.base.dao.GenericDao;

/**
 * @author zhangsy@sunwayworld.com
 * @date 2019-10-12
 */

public interface EmcOrderTaskDao extends GenericDao<EmcOrderTaskBean, Long> {

    List<EmcOrderTaskBean> selectContainerInfo(MapperParameter parameter);

    List<EmcOrderTaskBean> selectRunnableList(MapperParameter parameter);

    List<EmcOrderTaskBean> selectRunAnalysisOrderTaskList(MapperParameter parameter);

    List<Map<String, Object>> selectProjOrderCategoryByCondition(MapperParameter parameter);

    List<EmcOrderTaskBean> selectOrgProjOrderCategoryByCondition(MapperParameter parameter);

    List<EmcOrderTaskBean> selectOrderTaskList(MapperParameter parameter);

    List<Map<String, Object>> selectAnalysisDepartmentStatistics(MapperParameter parameter);

    List<Map<String, Object>> selectPersonnelWorkloadStatistics(MapperParameter parameter);

	List<EmcOrderTaskBean> selectOrderTaskByRunsList(MapperParameter parameter);

    List<EmcOrderTaskBean> selectUniqueOrderTask(MapperParameter parameter);

    List<EmcOrderTaskBean> selectSubpackageOrderTask(MapperParameter parameter);

    List<EmcOrderTaskBean> selectUnRunnableList(MapperParameter parameter);

    List<EmcOrderTaskBean> selectRunnableAnalAssignList(MapperParameter parameter);
}
