package com.sunwayworld.emc.module.bp.archive.dao.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.sunwayworld.emc.module.bp.archive.bean.EmcArchiveCategoryBean;
import com.sunwayworld.emc.module.bp.archive.dao.EmcArchiveCategoryDao;
import com.sunwayworld.emc.module.bp.archive.mapper.EmcArchiveCategoryMapper;
import com.sunwayworld.framework.mybatis.dao.MybatisDaoSupport;

/**
* @author zhengxy@sunwayworld.com 2020年10月31日
*/
@Repository
public class EmcArchiveCategoryDaoImpl extends MybatisDaoSupport<EmcArchiveCategoryBean, Long> implements EmcArchiveCategoryDao {

    @Autowired
    private EmcArchiveCategoryMapper emcArchiveCategoryMapper;

    @Override
    public EmcArchiveCategoryMapper getMapper() {
        return emcArchiveCategoryMapper;
    }
}
