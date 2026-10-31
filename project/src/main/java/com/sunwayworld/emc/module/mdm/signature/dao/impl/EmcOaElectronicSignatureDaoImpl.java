package com.sunwayworld.emc.module.mdm.signature.dao.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.sunwayworld.emc.module.mdm.signature.bean.EmcOaElectronicSignatureBean;
import com.sunwayworld.emc.module.mdm.signature.dao.EmcOaElectronicSignatureDao;
import com.sunwayworld.emc.module.mdm.signature.mapper.EmcOaElectronicSignatureMapper;
import com.sunwayworld.framework.mybatis.dao.MybatisDaoSupport;

/**
* @author zhengxy@sunwayworld.com 2020年12月30日
*/
@Repository
public class EmcOaElectronicSignatureDaoImpl extends MybatisDaoSupport<EmcOaElectronicSignatureBean, Long> implements EmcOaElectronicSignatureDao {

    @Autowired
    private EmcOaElectronicSignatureMapper emcOaElectronicSignatureMapper;

    @Override
    public EmcOaElectronicSignatureMapper getMapper() {
        return emcOaElectronicSignatureMapper;
    }
}
