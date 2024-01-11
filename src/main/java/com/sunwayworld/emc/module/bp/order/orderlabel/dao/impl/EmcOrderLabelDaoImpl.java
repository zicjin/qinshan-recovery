package com.sunwayworld.emc.module.bp.order.orderlabel.dao.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.sunwayworld.emc.module.bp.order.orderlabel.bean.EmcOrderLabelBean;
import com.sunwayworld.emc.module.bp.order.orderlabel.dao.EmcOrderLabelDao;
import com.sunwayworld.emc.module.bp.order.orderlabel.mapper.EmcOrderLabelMapper;
import com.sunwayworld.framework.mybatis.dao.MybatisDaoSupport;

/**
* @author zhengxy@sunwayworld.com 2019年10月18日
*/
@Repository
public class EmcOrderLabelDaoImpl extends MybatisDaoSupport<EmcOrderLabelBean, Long> implements EmcOrderLabelDao {

    @Autowired
    private EmcOrderLabelMapper emcOrderLabelMapper;

    @Override
    public EmcOrderLabelMapper getMapper() {
        return emcOrderLabelMapper;
    }
}
