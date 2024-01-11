package com.sunwayworld.emc.module.mdm.basemdm.mtl.dao.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.sunwayworld.emc.module.mdm.basemdm.mtl.bean.EmcMtlMaintainItemBean;
import com.sunwayworld.emc.module.mdm.basemdm.mtl.dao.EmcMtlMaintainItemDao;
import com.sunwayworld.emc.module.mdm.basemdm.mtl.mapper.EmcMtlMaintainItemMapper;
import com.sunwayworld.framework.mybatis.dao.MybatisDaoSupport;

/**
 * @author baogx@sunwayworld.com
 *
 * @date 2019年9月21日 下午3:34:45
 */
@Repository
public class EmcMtlMaintainItemDaoImpl extends MybatisDaoSupport<EmcMtlMaintainItemBean, Long> implements EmcMtlMaintainItemDao {

    @Autowired
    private EmcMtlMaintainItemMapper emcMtlMaintainItemMapper;

    @Override
    public EmcMtlMaintainItemMapper getMapper() {
        return emcMtlMaintainItemMapper;
    }
}
