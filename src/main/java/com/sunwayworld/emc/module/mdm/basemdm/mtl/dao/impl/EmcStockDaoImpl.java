package com.sunwayworld.emc.module.mdm.basemdm.mtl.dao.impl;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.sunwayworld.emc.module.mdm.basemdm.mtl.bean.EmcStockBean;
import com.sunwayworld.emc.module.mdm.basemdm.mtl.dao.EmcStockDao;
import com.sunwayworld.emc.module.mdm.basemdm.mtl.mapper.EmcStockMapper;
import com.sunwayworld.framework.mybatis.dao.MybatisDaoSupport;
import com.sunwayworld.framework.mybatis.mapper.MapperParameter;
import com.sunwayworld.framework.spring.annotation.GikamBean;

/**
 * @author baogx@sunwayworld.com
 *
 * @date 2019年9月20日 下午3:26:18
 */
@Repository
@GikamBean
public class EmcStockDaoImpl extends MybatisDaoSupport<EmcStockBean, Long> implements EmcStockDao {

    @Autowired
    private EmcStockMapper emcStockMapper;
    
    @Override
    public EmcStockMapper getMapper() {
        return emcStockMapper;
    }

    @Override
    public List<Map<String, Object>> selectStockLowLine(MapperParameter parameter) {
        return getMapper().selectStockLowLine(parameter);
    }
    
    
    @Override
    public List<Map<String, Object>> selectStockOverdue(MapperParameter parameter) {
        return getMapper().selectStockOverdue(parameter);
    }

    @Override
    public List<Map<String, Object>> selectQcStdStockPagination(MapperParameter parameter) {
        return getMapper().selectQcStdStockPagination(parameter);
    }
}
