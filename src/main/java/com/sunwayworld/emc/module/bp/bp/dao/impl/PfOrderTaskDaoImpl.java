package com.sunwayworld.emc.module.bp.bp.dao.impl;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.sunwayworld.emc.module.bp.bp.bean.EmcOrderTaskBean;
import com.sunwayworld.emc.module.bp.bp.dao.PfOrderTaskDao;
import com.sunwayworld.emc.module.bp.bp.mapper.PfOrderTaskMapper;
import com.sunwayworld.framework.mybatis.mapper.MapperParameter;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import com.sunwayworld.framework.support.PersistableHelper;

/**
 * @author zhengxy@sunwayworld.com 2022年03月24日
 */
@Repository
@GikamBean
public class PfOrderTaskDaoImpl extends EmcOrderTaskDaoImpl implements PfOrderTaskDao {

    @Autowired
    private PfOrderTaskMapper orderTaskMapper;

    @Override
    public List<EmcOrderTaskBean> selectUniqueOrderList(MapperParameter parameter) {
        return orderTaskMapper.selectUniqueOrderList(parameter).stream().map(m -> PersistableHelper.mapToPersistable(m, EmcOrderTaskBean.class)).collect(Collectors.toList());
    }

    @Override
    public List<EmcOrderTaskBean> selectTaskGroupByTest(MapperParameter parameter) {
        return orderTaskMapper.selectTaskGroupByTest(parameter).stream().map(m -> PersistableHelper.mapToPersistable(m, EmcOrderTaskBean.class)).collect(Collectors.toList());
    }

    @Override
    public List<EmcOrderTaskBean> selectOrderContainerInfo(MapperParameter parameter) {
        return orderTaskMapper.selectOrderContainerInfo(parameter).stream().map(m -> PersistableHelper.mapToPersistable(m, EmcOrderTaskBean.class)).collect(Collectors.toList());
    }

    @Override
    public List<EmcOrderTaskBean> selectOrderTaskPagination(MapperParameter parameter) {
        return orderTaskMapper.selectOrderTaskPagination(parameter).stream().map(m -> PersistableHelper.mapToPersistable(m, EmcOrderTaskBean.class)).collect(Collectors.toList());
    }
}
