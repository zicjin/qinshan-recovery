package com.sunwayworld.emc.module.mdm.basemdm.cert.dao.impl;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.sunwayworld.emc.module.mdm.basemdm.cert.bean.EmcCertBean;
import com.sunwayworld.emc.module.mdm.basemdm.cert.dao.EmcCertDao;
import com.sunwayworld.emc.module.mdm.basemdm.cert.mapper.EmcCertMapper;
import com.sunwayworld.framework.mybatis.dao.MybatisDaoSupport;
import com.sunwayworld.framework.mybatis.mapper.MapperParameter;

/**
 * @author zhangsy@sunwayworld.com
 * @date 2019-10-08
 */
@Repository
public class EmcCertDaoImpl extends MybatisDaoSupport<EmcCertBean, Long>implements EmcCertDao {

    @Autowired
    private EmcCertMapper emcCertMapper;

    @Override
    public EmcCertMapper getMapper() {
        return emcCertMapper;
    }

    @Override
    public List<Map<String, Object>> selectOverdueCert(MapperParameter parameter) {
        return getMapper().selectOverdueCert(parameter);
    }

    @Override
    public List<Map<String, Object>> selectOverdueQual(MapperParameter parameter) {
        return getMapper().selectOverdueQual(parameter);
    }

}
