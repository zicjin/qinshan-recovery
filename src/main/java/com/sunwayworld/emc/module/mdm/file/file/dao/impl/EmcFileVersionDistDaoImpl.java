package com.sunwayworld.emc.module.mdm.file.file.dao.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.sunwayworld.emc.module.mdm.file.file.bean.EmcFileVersionDistBean;
import com.sunwayworld.emc.module.mdm.file.file.dao.EmcFileVersionDistDao;
import com.sunwayworld.emc.module.mdm.file.file.mapper.EmcFileVersionDistMapper;
import com.sunwayworld.framework.mybatis.dao.MybatisDaoSupport;

/**
 * @author zhengxy@sunwayworld.com 2019年10月09日
 */
@Repository
public class EmcFileVersionDistDaoImpl extends MybatisDaoSupport<EmcFileVersionDistBean, Long> implements EmcFileVersionDistDao {

    @Autowired
    private EmcFileVersionDistMapper emcFileVersionDistMapper;

    @Override
    public EmcFileVersionDistMapper getMapper() {
        return emcFileVersionDistMapper;
    }
}
