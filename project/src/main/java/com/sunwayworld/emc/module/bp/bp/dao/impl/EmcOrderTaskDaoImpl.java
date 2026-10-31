package com.sunwayworld.emc.module.bp.bp.dao.impl;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.sunwayworld.emc.module.bp.bp.bean.EmcOrderTaskBean;
import com.sunwayworld.emc.module.bp.bp.dao.EmcOrderTaskDao;
import com.sunwayworld.emc.module.bp.bp.mapper.EmcOrderTaskMapper;
import com.sunwayworld.framework.mybatis.dao.MybatisDaoSupport;
import com.sunwayworld.framework.mybatis.mapper.MapperParameter;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import com.sunwayworld.framework.support.PersistableHelper;

/**
 * @author zhangsy@sunwayworld.com
 * @date 2019-10-12
 */
@Repository
@GikamBean
public class EmcOrderTaskDaoImpl extends MybatisDaoSupport<EmcOrderTaskBean, Long> implements EmcOrderTaskDao {

    @Autowired
    private EmcOrderTaskMapper emcOrderTaskMapper;

    @Override
    public EmcOrderTaskMapper getMapper() {
        return emcOrderTaskMapper;
    }

    @Override
    public List<EmcOrderTaskBean> selectContainerInfo(MapperParameter parameter) {
        return getMapper().selectContainerInfo(parameter).stream().map(m -> PersistableHelper.mapToPersistable(m, EmcOrderTaskBean.class)).collect(Collectors.toList());
    }

    @Override
    public List<EmcOrderTaskBean> selectRunnableList(MapperParameter parameter) {
        return getMapper().selectRunnableList(parameter).stream().map(m -> PersistableHelper.mapToPersistable(m, EmcOrderTaskBean.class)).collect(Collectors.toList());
    }

    @Override
    public List<EmcOrderTaskBean> selectRunAnalysisOrderTaskList(MapperParameter parameter) {
        return getMapper().selectRunAnalysisOrderTaskList(parameter).stream().map(m -> PersistableHelper.mapToPersistable(m, EmcOrderTaskBean.class)).collect(Collectors.toList());
    }

    @Override
    public List<Map<String, Object>> selectProjOrderCategoryByCondition(MapperParameter parameter) {
        return getMapper().selectProjOrderCategoryByCondition(parameter);
    }

    @Override
    public List<EmcOrderTaskBean> selectOrgProjOrderCategoryByCondition(MapperParameter parameter) {
        return getMapper().selectOrgProjOrderCategoryByCondition(parameter);
    }

    @Override
    public List<EmcOrderTaskBean> selectOrderTaskList(MapperParameter parameter) {
        return getMapper().selectOrderTaskList(parameter).stream().map(m -> PersistableHelper.mapToPersistable(m, EmcOrderTaskBean.class)).collect(Collectors.toList());
    }

    @Override
    public List<EmcOrderTaskBean> selectOrderTaskByRunsList(MapperParameter parameter) {
        return getMapper().selectOrderTaskByRunsList(parameter).stream().map(m -> PersistableHelper.mapToPersistable(m, EmcOrderTaskBean.class)).collect(Collectors.toList());
    }

    @Override
    public List<EmcOrderTaskBean> selectUniqueOrderTask(MapperParameter parameter) {
        return getMapper().selectUniqueOrderTask(parameter).stream().map(m -> PersistableHelper.mapToPersistable(m, EmcOrderTaskBean.class)).collect(Collectors.toList());
    }

    @Override
    public List<EmcOrderTaskBean> selectSubpackageOrderTask(MapperParameter parameter) {
        return getMapper().selectSubpackageOrderTask(parameter).stream().map(m -> PersistableHelper.mapToPersistable(m, EmcOrderTaskBean.class)).collect(Collectors.toList());
    }

    @Override
    public List<EmcOrderTaskBean> selectUnRunnableList(MapperParameter parameter) {
        return  getMapper().selectUnRunnableList(parameter).stream().map(m -> PersistableHelper.mapToPersistable(m, EmcOrderTaskBean.class)).collect(Collectors.toList());
    }

    @Override
    public List<Map<String, Object>> selectAnalysisDepartmentStatistics(MapperParameter parameter) {
        return getMapper().selectAnalysisDepartmentStatistics(parameter);
    }

    @Override
    public List<Map<String, Object>> selectPersonnelWorkloadStatistics(MapperParameter parameter) {
        return getMapper().selectPersonnelWorkloadStatistics(parameter);
    }

    @Override
    public List<EmcOrderTaskBean> selectRunnableAnalAssignList(MapperParameter parameter) {
        return getMapper().selectRunnableAnalAssignList(parameter).stream().map(m -> PersistableHelper.mapToPersistable(m, EmcOrderTaskBean.class)).collect(Collectors.toList());
    }
}
