package com.sunwayworld.emc.module.bp.bp.dao.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.sunwayworld.emc.module.bp.bp.bean.EmcFolderBean;
import com.sunwayworld.emc.module.bp.bp.dao.EmcFolderDao;
import com.sunwayworld.emc.module.bp.bp.mapper.EmcFolderMapper;
import com.sunwayworld.framework.mybatis.dao.MybatisDaoSupport;

/**
 * @author zhangsy@sunwayworld.com
 * @date 2019-10-12
 */
@Repository
public class EmcFolderDaoImpl extends MybatisDaoSupport<EmcFolderBean, Long>implements EmcFolderDao {

    @Autowired
    private EmcFolderMapper emcFolderMapper;

    @Override
    public EmcFolderMapper getMapper() {
        return emcFolderMapper;
    }

}
