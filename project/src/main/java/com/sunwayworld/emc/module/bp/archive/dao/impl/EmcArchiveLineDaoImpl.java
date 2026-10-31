package com.sunwayworld.emc.module.bp.archive.dao.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.sunwayworld.emc.module.bp.archive.bean.EmcArchiveLineBean;
import com.sunwayworld.emc.module.bp.archive.dao.EmcArchiveLineDao;
import com.sunwayworld.emc.module.bp.archive.mapper.EmcArchiveLineMapper;
import com.sunwayworld.framework.mybatis.dao.MybatisDaoSupport;

/**
* @author zhengxy@sunwayworld.com 2020年10月31日
*/
@Repository
public class EmcArchiveLineDaoImpl extends MybatisDaoSupport<EmcArchiveLineBean, Long> implements EmcArchiveLineDao {

    @Autowired
    private EmcArchiveLineMapper emcArchiveLineMapper;

    @Override
    public EmcArchiveLineMapper getMapper() {
        return emcArchiveLineMapper;
    }
}
