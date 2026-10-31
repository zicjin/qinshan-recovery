package com.sunwayworld.emc.module.bp.project.project.dao.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.sunwayworld.emc.module.bp.project.project.bean.EmcProjectOtherFeeBean;
import com.sunwayworld.emc.module.bp.project.project.dao.EmcProjectOtherFeeDao;
import com.sunwayworld.emc.module.bp.project.project.mapper.EmcProjectOtherFeeMapper;
import com.sunwayworld.framework.mybatis.dao.MybatisDaoSupport;

/**
* @author zhengxy@sunwayworld.com 2022年01月14日
*/
@Repository
public class EmcProjectOtherFeeDaoImpl extends MybatisDaoSupport<EmcProjectOtherFeeBean, Long> implements EmcProjectOtherFeeDao {

    @Autowired
    private EmcProjectOtherFeeMapper projectOtherFeeMapper;

    @Override
    public EmcProjectOtherFeeMapper getMapper() {
        return projectOtherFeeMapper;
    }
}
