package com.sunwayworld.emc.module.mdm.basemdm.area.dao.impl;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.sunwayworld.emc.module.mdm.basemdm.area.bean.EmcAreaBean;
import com.sunwayworld.emc.module.mdm.basemdm.area.dao.EmcAreaDao;
import com.sunwayworld.emc.module.mdm.basemdm.area.mapper.EmcAreaMapper;
import com.sunwayworld.framework.mybatis.dao.MybatisDaoSupport;
import com.sunwayworld.framework.mybatis.mapper.MapperParameter;


/**
 * @author yuanhh@sunwayworld.com 2019年9月16日
 */
@Repository
public class EmcAreaDaoImpl extends MybatisDaoSupport<EmcAreaBean, Long>implements EmcAreaDao {
    
    @Autowired
    private EmcAreaMapper emcAreaMapper;
    
    @Override
    public EmcAreaMapper getMapper() {
        return emcAreaMapper;
    }
    
    @Override
    public List<Map<String, Object>> selectTreeNodeList(MapperParameter parameter) {
        return emcAreaMapper.selectTreeNodeList(parameter);
    }
}
