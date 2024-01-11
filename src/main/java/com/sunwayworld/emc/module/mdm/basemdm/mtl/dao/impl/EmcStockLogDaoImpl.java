package com.sunwayworld.emc.module.mdm.basemdm.mtl.dao.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.sunwayworld.emc.module.mdm.basemdm.mtl.bean.EmcStockLogBean;
import com.sunwayworld.emc.module.mdm.basemdm.mtl.dao.EmcStockLogDao;
import com.sunwayworld.emc.module.mdm.basemdm.mtl.mapper.EmcStockLogMapper;
import com.sunwayworld.framework.mybatis.dao.MybatisDaoSupport;

/**
 * @author baogx@sunwayworld.com
 *
 * @date 2019年9月20日 下午3:28:24
 */
@Repository
public class EmcStockLogDaoImpl extends MybatisDaoSupport<EmcStockLogBean, Long> implements EmcStockLogDao {

    @Autowired
    private EmcStockLogMapper emcStockLogMapper;
    
    @Override
    public EmcStockLogMapper getMapper() {
        return emcStockLogMapper;
    }
}
