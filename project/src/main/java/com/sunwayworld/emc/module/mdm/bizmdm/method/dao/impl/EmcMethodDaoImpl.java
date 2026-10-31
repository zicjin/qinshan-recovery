package com.sunwayworld.emc.module.mdm.bizmdm.method.dao.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.sunwayworld.emc.module.mdm.bizmdm.method.bean.EmcMethodBean;
import com.sunwayworld.emc.module.mdm.bizmdm.method.dao.EmcMethodDao;
import com.sunwayworld.emc.module.mdm.bizmdm.method.mapper.EmcMethodMapper;
import com.sunwayworld.framework.mybatis.dao.MybatisDaoSupport;

/**
 * @author yuanhh@sunwayworld.com 2019年9月10日
 */

@Repository
public class EmcMethodDaoImpl extends MybatisDaoSupport<EmcMethodBean, Long> implements EmcMethodDao {

    @Autowired
    private EmcMethodMapper emcMethodMapper;

    @Override
    public EmcMethodMapper getMapper() {
        return emcMethodMapper;
    }

    @Override
    public Long selectReportConfigLineIdById(Long id) {
        return getMapper().selectReportConfigLineIdById(id);
    }
}
