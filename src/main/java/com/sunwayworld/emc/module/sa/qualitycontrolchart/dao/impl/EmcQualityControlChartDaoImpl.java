package com.sunwayworld.emc.module.sa.qualitycontrolchart.dao.impl;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.sunwayworld.emc.module.bp.bp.bean.EmcOrderTaskBean;
import com.sunwayworld.emc.module.bp.bp.bean.EmcOrderTaskResultBean;
import com.sunwayworld.emc.module.sa.qualitycontrolchart.dao.EmcQualityControlChartDao;
import com.sunwayworld.emc.module.sa.qualitycontrolchart.mapper.EmcQualityControlChartMapper;
import com.sunwayworld.framework.mybatis.dao.MybatisDaoSupport;
import com.sunwayworld.framework.mybatis.mapper.MapperParameter;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import com.sunwayworld.framework.support.PersistableHelper;

@Repository
@GikamBean
public class EmcQualityControlChartDaoImpl extends MybatisDaoSupport<EmcOrderTaskResultBean, Long> implements EmcQualityControlChartDao {

    @Autowired
    private EmcQualityControlChartMapper emcQualityControlChartMapper;

    @Override
    public EmcQualityControlChartMapper getMapper() {
        return emcQualityControlChartMapper;
    }

    @Override
    public List<Map<String, Object>> selectOrderTaskResultPagination(MapperParameter parameter) {
        return getMapper().selectOrderTaskResultPagination(parameter);
    }

    @Override
    public List<EmcOrderTaskBean> selectTestSelectableList(MapperParameter parameter) {
        return getMapper().selectTestSelectableList(parameter).stream().map(m -> PersistableHelper.mapToPersistable(m, EmcOrderTaskBean.class)).collect(Collectors.toList());
    }

    @Override
    public List<EmcOrderTaskResultBean> selectItemSelectableList(MapperParameter parameter) {
        return getMapper().selectItemSelectableList(parameter).stream().map(m -> PersistableHelper.mapToPersistable(m, EmcOrderTaskResultBean.class)).collect(Collectors.toList());
    }

    @Override
    public List<EmcOrderTaskBean> selectQcCategorySelectableList(MapperParameter parameter) {
        return getMapper().selectQcCategorySelectableList(parameter).stream().map(m -> PersistableHelper.mapToPersistable(m, EmcOrderTaskBean.class)).collect(Collectors.toList());
    }

    @Override
    public List<EmcOrderTaskBean> selectUserSelectableList(MapperParameter parameter) {
        return getMapper().selectUserSelectableList(parameter).stream().map(m -> PersistableHelper.mapToPersistable(m, EmcOrderTaskBean.class)).collect(Collectors.toList());
    }

}
