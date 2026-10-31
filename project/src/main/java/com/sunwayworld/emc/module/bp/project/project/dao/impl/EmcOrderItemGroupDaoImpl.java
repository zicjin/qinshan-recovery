package com.sunwayworld.emc.module.bp.project.project.dao.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.sunwayworld.emc.module.bp.project.project.bean.EmcOrderItemGroupBean;
import com.sunwayworld.emc.module.bp.project.project.dao.EmcOrderItemGroupDao;
import com.sunwayworld.emc.module.bp.project.project.mapper.EmcOrderItemGroupMapper;
import com.sunwayworld.framework.mybatis.dao.MybatisDaoSupport;
import com.sunwayworld.framework.spring.annotation.GikamBean;

@Repository
@GikamBean
public class EmcOrderItemGroupDaoImpl extends MybatisDaoSupport<EmcOrderItemGroupBean, Long>implements EmcOrderItemGroupDao {

    @Autowired
    private EmcOrderItemGroupMapper orderItemGroupMapper;

    @Override
    public EmcOrderItemGroupMapper getMapper() {
        return orderItemGroupMapper;
    }

}
