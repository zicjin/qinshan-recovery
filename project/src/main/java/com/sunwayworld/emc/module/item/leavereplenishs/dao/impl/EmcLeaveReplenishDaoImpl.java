package com.sunwayworld.emc.module.item.leavereplenishs.dao.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.sunwayworld.emc.module.item.leavereplenishs.bean.EmcLeaveReplenishBean;
import com.sunwayworld.emc.module.item.leavereplenishs.dao.EmcLeaveReplenishDao;
import com.sunwayworld.emc.module.item.leavereplenishs.mapper.EmcLeaveReplenishMapper;
import com.sunwayworld.framework.mybatis.dao.MybatisDaoSupport;
import com.sunwayworld.framework.spring.annotation.GikamBean;


/**
 * @author zhangft@sunwayworld.com
 * @date 2019-11-14
 */
@Repository
@GikamBean
public class EmcLeaveReplenishDaoImpl extends MybatisDaoSupport<EmcLeaveReplenishBean, String> implements EmcLeaveReplenishDao {
    
    @Autowired
    private EmcLeaveReplenishMapper leaveReplenishMapper;
    
    @Override
    public EmcLeaveReplenishMapper getMapper() {
        return leaveReplenishMapper;
    }
    
}
