package com.sunwayworld.emc.module.bp.edit.projectchange.dao.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.sunwayworld.emc.module.bp.edit.projectchange.bean.EmcOrderItemGroupChangeBean;
import com.sunwayworld.emc.module.bp.edit.projectchange.dao.EmcOrderItemGroupChangeDao;
import com.sunwayworld.emc.module.bp.edit.projectchange.mapper.EmcOrderItemGroupChangeMapper;
import com.sunwayworld.framework.mybatis.dao.MybatisDaoSupport;
import com.sunwayworld.framework.spring.annotation.GikamBean;

@Repository
@GikamBean
public class EmcOrderItemGroupChangeDaoImpl extends MybatisDaoSupport<EmcOrderItemGroupChangeBean, Long>implements EmcOrderItemGroupChangeDao {

    @Autowired
    private EmcOrderItemGroupChangeMapper orderItemGroupMapper;

    @Override
    public EmcOrderItemGroupChangeMapper getMapper() {
        return orderItemGroupMapper;
    }

}
