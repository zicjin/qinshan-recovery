package com.sunwayworld.emc.module.bp.delivery.dao.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.sunwayworld.emc.module.bp.delivery.bean.EmcDeliveryBean;
import com.sunwayworld.emc.module.bp.delivery.dao.EmcDeliveryDao;
import com.sunwayworld.emc.module.bp.delivery.mapper.EmcDeliveryMapper;
import com.sunwayworld.framework.mybatis.dao.MybatisDaoSupport;
import com.sunwayworld.framework.spring.annotation.GikamBean;

/**
 * @author zhangsy@sunwayworld.com
 * @date 2019-10-12
 */
@Repository
@GikamBean
public class EmcDeliveryDaoImpl extends MybatisDaoSupport<EmcDeliveryBean, Long> implements EmcDeliveryDao {

    @Autowired
    private EmcDeliveryMapper deliveryMapper;

    @Override
    public EmcDeliveryMapper getMapper() {
        return deliveryMapper;
    }

}
