package com.sunwayworld.emc.module.item.leaveapplies.dao.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.sunwayworld.emc.module.item.leaveapplies.bean.EmcLeaveApplyBean;
import com.sunwayworld.emc.module.item.leaveapplies.dao.EmcLeaveApplyDao;
import com.sunwayworld.emc.module.item.leaveapplies.mapper.EmcLeaveApplyMapper;
import com.sunwayworld.framework.mybatis.dao.MybatisDaoSupport;
import com.sunwayworld.framework.spring.annotation.GikamBean;


/**
 * @author zhangft@sunwayworld.com
 * @date 2019-11-12
 */
@Repository
@GikamBean
public class EmcLeaveApplyDaoImpl extends MybatisDaoSupport<EmcLeaveApplyBean, String> implements EmcLeaveApplyDao {
    
    @Autowired
    private EmcLeaveApplyMapper leaveApplyMapper;
    
    @Override
    public EmcLeaveApplyMapper getMapper() {
        return leaveApplyMapper;
    }
    
}
