package com.sunwayworld.emc.module.bp.bp.mapper;

import java.util.List;
import java.util.Map;

import com.sunwayworld.emc.module.bp.bp.bean.EmcOrderTaskBean;
import com.sunwayworld.framework.mybatis.mapper.MapperParameter;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import com.sunwayworld.framework.support.base.mapper.GenericMapper;

/**
 * @author zhangsy@sunwayworld.com
 * @date 2019-10-12
 */
@GikamBean
public interface EmcOrderTaskMapper extends GenericMapper<Long> {
    List<Map<String, Object>> selectContainerInfo(MapperParameter parameter);

    List<Map<String, Object>> selectRunnableList(MapperParameter parameter);

    List<Map<String, Object>> selectRunAnalysisOrderTaskList(MapperParameter parameter);

    List<Map<String, Object>> selectProjOrderCategoryByCondition(MapperParameter parameter);

    List<EmcOrderTaskBean> selectOrgProjOrderCategoryByCondition(MapperParameter parameter);

    List<Map<String, Object>> selectOrderTaskList(MapperParameter parameter);

    List<Map<String, Object>> selectAnalysisDepartmentStatistics(MapperParameter parameter);

    List<Map<String, Object>> selectPersonnelWorkloadStatistics(MapperParameter parameter);

	List<Map<String, Object>> selectOrderTaskByRunsList(MapperParameter parameter);

    List<Map<String, Object>> selectUniqueOrderTask(MapperParameter parameter);

    List<Map<String, Object>> selectSubpackageOrderTask(MapperParameter parameter);

    List<Map<String, Object>> selectUnRunnableList(MapperParameter parameter);

    List<Map<String, Object>> selectRunnableAnalAssignList(MapperParameter parameter);

}
