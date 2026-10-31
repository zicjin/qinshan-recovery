package com.sunwayworld.emc.module.mdm.basemdm.traincourse.dao.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.sunwayworld.emc.module.mdm.basemdm.traincourse.bean.EmcTcTestMethodBean;
import com.sunwayworld.emc.module.mdm.basemdm.traincourse.dao.EmcTcTestMethodDao;
import com.sunwayworld.emc.module.mdm.basemdm.traincourse.mapper.EmcTcTestMethodMapper;
import com.sunwayworld.framework.mybatis.dao.MybatisDaoSupport;

/**
 * @author magh@sunwayworld.com
 * @date 2019-10-28
 */
@Repository
public class EmcTcTestMethodDaoImpl extends MybatisDaoSupport<EmcTcTestMethodBean, Long> implements EmcTcTestMethodDao {

    @Autowired
    private EmcTcTestMethodMapper emcTcTestMethodMapper;

    @Override
    public EmcTcTestMethodMapper getMapper() {
        return emcTcTestMethodMapper;
    }

}
