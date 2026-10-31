package com.sunwayworld.emc.module.mdm.file.file.dao.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.sunwayworld.emc.module.mdm.file.file.bean.EmcFileVersionBean;
import com.sunwayworld.emc.module.mdm.file.file.dao.EmcFileVersionDao;
import com.sunwayworld.emc.module.mdm.file.file.mapper.EmcFileVersionMapper;
import com.sunwayworld.framework.mybatis.dao.MybatisDaoSupport;

/**
 * @author zhengxy@sunwayworld.com 2019年10月09日
 */
@Repository
public class EmcFileVersionDaoImpl extends MybatisDaoSupport<EmcFileVersionBean, Long> implements EmcFileVersionDao {

    @Autowired
    private EmcFileVersionMapper emcFileVersionMapper;

    @Override
    public EmcFileVersionMapper getMapper() {
        return emcFileVersionMapper;
    }
}
