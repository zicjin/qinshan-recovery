package com.sunwayworld.emc.module.bp.order.orderretain.dao.impl;

import com.sunwayworld.emc.module.bp.order.orderretain.bean.vo.EmcOrderRetainOverdueBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.sunwayworld.emc.module.bp.order.orderretain.bean.EmcOrderRetainBean;
import com.sunwayworld.emc.module.bp.order.orderretain.dao.EmcOrderRetainDao;
import com.sunwayworld.emc.module.bp.order.orderretain.mapper.EmcOrderRetainMapper;
import com.sunwayworld.framework.mybatis.dao.MybatisDaoSupport;

import java.util.List;

/**
* @author zhengxy@sunwayworld.com 2019年10月17日
*/
@Repository
public class EmcOrderRetainDaoImpl extends MybatisDaoSupport<EmcOrderRetainBean, Long> implements EmcOrderRetainDao {

    @Autowired
    private EmcOrderRetainMapper emcOrderRetainMapper;

    @Override
    public EmcOrderRetainMapper getMapper() {
        return emcOrderRetainMapper;
    }

    @Override
    public List<EmcOrderRetainOverdueBean> selectOverdueOrderRetain() {
        return getMapper().selectOverdueOrderRetain();
    }

}
