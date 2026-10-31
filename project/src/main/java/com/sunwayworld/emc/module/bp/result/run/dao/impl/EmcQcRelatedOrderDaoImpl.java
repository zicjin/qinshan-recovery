package com.sunwayworld.emc.module.bp.result.run.dao.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.sunwayworld.emc.module.bp.result.run.bean.EmcQcRelatedOrderBean;
import com.sunwayworld.emc.module.bp.result.run.dao.EmcQcRelatedOrderDao;
import com.sunwayworld.emc.module.bp.result.run.mapper.EmcQcRelatedOrderMapper;
import com.sunwayworld.framework.mybatis.dao.MybatisDaoSupport;

/**
* @author zhengxy@sunwayworld.com 2021年12月08日
*/
@Repository
public class EmcQcRelatedOrderDaoImpl extends MybatisDaoSupport<EmcQcRelatedOrderBean, Long> implements EmcQcRelatedOrderDao {

    @Autowired
    private EmcQcRelatedOrderMapper emcQcRelatedOrderMapper;

    @Override
    public EmcQcRelatedOrderMapper getMapper() {
        return emcQcRelatedOrderMapper;
    }
}
