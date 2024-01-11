package com.sunwayworld.emc.module.mdm.basemdm.mtl.dao.impl;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.sunwayworld.emc.module.mdm.basemdm.mtl.bean.EmcStockConcentBean;
import com.sunwayworld.emc.module.mdm.basemdm.mtl.dao.EmcStockConcentDao;
import com.sunwayworld.emc.module.mdm.basemdm.mtl.mapper.EmcStockConcentMapper;
import com.sunwayworld.framework.mybatis.dao.MybatisDaoSupport;
import com.sunwayworld.framework.mybatis.mapper.MapperParameter;

/**
 * @author baogx@sunwayworld.com
 *
 * @date 2019年9月21日 下午3:37:39
 */
@Repository
public class EmcStockConcentDaoImpl extends MybatisDaoSupport<EmcStockConcentBean, Long>implements EmcStockConcentDao {

    @Autowired
    private EmcStockConcentMapper emcStockConcentMapper;

    @Override
    public EmcStockConcentMapper getMapper() {
        return emcStockConcentMapper;
    }

    @Override
    public List<Map<String, Object>> selectLimitByCondition(MapperParameter parameter) {
        return getMapper().selectLimitByCondition(parameter);
    }

}
