package com.sunwayworld.emc.module.bp.project.project.dao.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.sunwayworld.emc.module.bp.project.project.bean.EmcOrderItemBean;
import com.sunwayworld.emc.module.bp.project.project.dao.EmcOrderItemDao;
import com.sunwayworld.emc.module.bp.project.project.mapper.EmcOrderItemMapper;
import com.sunwayworld.framework.mybatis.dao.MybatisDaoSupport;

@Repository
public class EmcOrderItemDaoImpl extends MybatisDaoSupport<EmcOrderItemBean, Long>implements EmcOrderItemDao {

    @Autowired
    private EmcOrderItemMapper orderItemMapper;

    @Override
    public EmcOrderItemMapper getMapper() {
        return orderItemMapper;
    }

}
