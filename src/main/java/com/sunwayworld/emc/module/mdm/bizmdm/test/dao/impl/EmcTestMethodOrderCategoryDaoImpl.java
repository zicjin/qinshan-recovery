package com.sunwayworld.emc.module.mdm.bizmdm.test.dao.impl;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.sunwayworld.emc.module.mdm.bizmdm.test.bean.EmcTestMethodOrderCategoryBean;
import com.sunwayworld.emc.module.mdm.bizmdm.test.dao.EmcTestMethodOrderCategoryDao;
import com.sunwayworld.emc.module.mdm.bizmdm.test.mapper.EmcTestMethodOrderCategoryMapper;
import com.sunwayworld.framework.mybatis.dao.MybatisDaoSupport;
import com.sunwayworld.framework.mybatis.mapper.MapperParameter;


/**
 * @author yuanhh@sunwayworld.com
 * @date 2019-09-19
 */
@Repository
public class EmcTestMethodOrderCategoryDaoImpl extends MybatisDaoSupport<EmcTestMethodOrderCategoryBean, Long>implements EmcTestMethodOrderCategoryDao {
    
    @Autowired
    private EmcTestMethodOrderCategoryMapper emcTestMethodOrderCategoryMapper;
    
    @Override
    public EmcTestMethodOrderCategoryMapper getMapper() {
        return emcTestMethodOrderCategoryMapper;
    }
    
    @Override
    public List<Map<String, Object>> selectMaintenancePage(MapperParameter parameter) {
        return getMapper().selectMaintenancePage(parameter);
    }
}
