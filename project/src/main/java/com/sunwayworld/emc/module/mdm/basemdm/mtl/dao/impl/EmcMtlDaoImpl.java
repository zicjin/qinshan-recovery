package com.sunwayworld.emc.module.mdm.basemdm.mtl.dao.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.sunwayworld.emc.module.mdm.basemdm.mtl.bean.EmcMtlBean;
import com.sunwayworld.emc.module.mdm.basemdm.mtl.dao.EmcMtlDao;
import com.sunwayworld.emc.module.mdm.basemdm.mtl.mapper.EmcMtlMapper;
import com.sunwayworld.framework.mybatis.dao.MybatisDaoSupport;
import com.sunwayworld.framework.spring.annotation.GikamBean;
/**
 * @author baogx@sunwayworld.com
 *
 * @date 2019年9月19日 下午6:13:22
 */
@Repository
@GikamBean
public class EmcMtlDaoImpl extends MybatisDaoSupport<EmcMtlBean, String> implements EmcMtlDao {

    @Autowired
    private EmcMtlMapper emcMtlMapper;

    @Override
    public EmcMtlMapper getMapper() {
        return emcMtlMapper;
    }

    @Override
    public void updateStockQty(String id) {
        getMapper().updateStockQty(id);
    }
}
