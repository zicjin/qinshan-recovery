package com.sunwayworld.emc.module.mdm.basemdm.mtl.dao.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.sunwayworld.emc.module.mdm.basemdm.mtl.bean.EmcMtlMaintainBean;
import com.sunwayworld.emc.module.mdm.basemdm.mtl.dao.EmcMtlMaintainDao;
import com.sunwayworld.emc.module.mdm.basemdm.mtl.mapper.EmcMtlMaintainMapper;
import com.sunwayworld.framework.mybatis.dao.MybatisDaoSupport;
/**
 * @author baogx@sunwayworld.com
 *
 * @date 2019年9月21日 下午3:31:21
 */
@Repository
public class EmcMtlMaintainDaoImpl extends MybatisDaoSupport<EmcMtlMaintainBean, Long> implements EmcMtlMaintainDao {

    @Autowired
    private EmcMtlMaintainMapper emcMtlMaintainMapper;

    @Override
    public EmcMtlMaintainMapper getMapper() {
        return emcMtlMaintainMapper;
    }
}
