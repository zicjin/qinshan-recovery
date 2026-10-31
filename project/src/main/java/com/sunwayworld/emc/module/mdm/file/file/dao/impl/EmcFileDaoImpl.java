package com.sunwayworld.emc.module.mdm.file.file.dao.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.sunwayworld.emc.module.mdm.file.file.bean.EmcFileBean;
import com.sunwayworld.emc.module.mdm.file.file.dao.EmcFileDao;
import com.sunwayworld.emc.module.mdm.file.file.mapper.EmcFileMapper;
import com.sunwayworld.framework.mybatis.dao.MybatisDaoSupport;

/**
 * @author zhengxy@sunwayworld.com 2019年10月09日
 */
@Repository
public class EmcFileDaoImpl extends MybatisDaoSupport<EmcFileBean, Long> implements EmcFileDao {

    @Autowired
    private EmcFileMapper emcFileMapper;

    @Override
    public EmcFileMapper getMapper() {
        return emcFileMapper;
    }
}
