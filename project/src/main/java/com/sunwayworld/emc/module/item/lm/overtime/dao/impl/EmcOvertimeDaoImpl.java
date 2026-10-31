package com.sunwayworld.emc.module.item.lm.overtime.dao.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.sunwayworld.emc.module.item.lm.overtime.bean.EmcOvertimeBean;
import com.sunwayworld.emc.module.item.lm.overtime.dao.EmcOvertimeDao;
import com.sunwayworld.emc.module.item.lm.overtime.mapper.EmcOvertimeMapper;
import com.sunwayworld.framework.mybatis.dao.MybatisDaoSupport;

/**
 * @author cuikx@sunwayworld.com
 * @date 2019-11-14
 */
@Repository
public class EmcOvertimeDaoImpl extends MybatisDaoSupport<EmcOvertimeBean, Long> implements EmcOvertimeDao {

    @Autowired
    private EmcOvertimeMapper emcOvertimeMapper;

    @Override
    public EmcOvertimeMapper getMapper() {
        return emcOvertimeMapper;
    }

}
