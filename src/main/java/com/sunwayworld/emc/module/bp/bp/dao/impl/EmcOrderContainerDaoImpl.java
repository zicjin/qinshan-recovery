package com.sunwayworld.emc.module.bp.bp.dao.impl;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.sunwayworld.emc.module.bp.bp.bean.EmcOrderContainerBean;
import com.sunwayworld.emc.module.bp.bp.dao.EmcOrderContainerDao;
import com.sunwayworld.emc.module.bp.bp.mapper.EmcOrderContainerMapper;
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
public class EmcOrderContainerDaoImpl extends MybatisDaoSupport<EmcOrderContainerBean, Long> implements EmcOrderContainerDao {

    @Autowired
    private EmcOrderContainerMapper emcOrderContainerMapper;

    @Override
    public EmcOrderContainerMapper getMapper() {
        return emcOrderContainerMapper;
    }

    @Override
    public List<EmcOrderContainerBean> selectOrderContainerList(MapperParameter parameter) {
        return getMapper().selectOrderContainerList(parameter).stream().map(m -> PersistableHelper.mapToPersistable(m, EmcOrderContainerBean.class)).collect(Collectors.toList());
    }

    @Override
    public List<EmcOrderContainerBean> selectOrderReceiveContainerList(MapperParameter parameter) {
        return getMapper().selectOrderReceiveContainerList(parameter).stream().map(m -> PersistableHelper.mapToPersistable(m, EmcOrderContainerBean.class)).collect(Collectors.toList());
    }
}
