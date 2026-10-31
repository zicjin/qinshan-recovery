package com.sunwayworld.emc.module.mdm.bizmdm.client.dao.impl;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.sunwayworld.emc.module.mdm.bizmdm.client.bean.EmcClientBean;
import com.sunwayworld.emc.module.mdm.bizmdm.client.dao.EmcClientDao;
import com.sunwayworld.emc.module.mdm.bizmdm.client.mapper.EmcClientMapper;
import com.sunwayworld.framework.mybatis.dao.MybatisDaoSupport;
import com.sunwayworld.framework.mybatis.mapper.MapperParameter;

/**
 * @author magh@sunwayworld.com
 * @date 2019-10-19
 */
@Repository
public class EmcClientDaoImpl extends MybatisDaoSupport<EmcClientBean, Long>implements EmcClientDao {

    @Autowired
    private EmcClientMapper emcClientMapper;

    @Override
    public EmcClientMapper getMapper() {
        return emcClientMapper;
    }

    @Override
    public List<Map<String, Object>> selectClientByCondition(MapperParameter parameter) {
        return getMapper().selectClientByCondition(parameter);
    }
}
