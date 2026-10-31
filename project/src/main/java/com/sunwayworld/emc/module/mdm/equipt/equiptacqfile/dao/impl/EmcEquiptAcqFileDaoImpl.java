package com.sunwayworld.emc.module.mdm.equipt.equiptacqfile.dao.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.sunwayworld.emc.module.mdm.equipt.equiptacqfile.bean.EmcEquiptAcqFileBean;
import com.sunwayworld.emc.module.mdm.equipt.equiptacqfile.dao.EmcEquiptAcqFileDao;
import com.sunwayworld.emc.module.mdm.equipt.equiptacqfile.mapper.EmcEquiptAcqFileMapper;
import com.sunwayworld.framework.mybatis.dao.MybatisDaoSupport;
import com.sunwayworld.framework.spring.annotation.GikamBean;

@Repository
@GikamBean
public class EmcEquiptAcqFileDaoImpl extends MybatisDaoSupport<EmcEquiptAcqFileBean, Long> implements EmcEquiptAcqFileDao {

    @Autowired
    private EmcEquiptAcqFileMapper emcEquiptAcqFileMapper;

    @Override
    public EmcEquiptAcqFileMapper getMapper() {
        return emcEquiptAcqFileMapper;
    }

}
