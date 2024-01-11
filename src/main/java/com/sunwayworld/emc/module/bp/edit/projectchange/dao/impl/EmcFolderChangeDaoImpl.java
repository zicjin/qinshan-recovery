package com.sunwayworld.emc.module.bp.edit.projectchange.dao.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.sunwayworld.emc.module.bp.edit.projectchange.bean.EmcFolderChangeBean;
import com.sunwayworld.emc.module.bp.edit.projectchange.dao.EmcFolderChangeDao;
import com.sunwayworld.emc.module.bp.edit.projectchange.mapper.EmcFolderChangeMapper;
import com.sunwayworld.framework.mybatis.dao.MybatisDaoSupport;

/**
 * @author zhangsy@sunwayworld.com
 * @date 2019-10-12
 */
@Repository
public class EmcFolderChangeDaoImpl extends MybatisDaoSupport<EmcFolderChangeBean, Long>implements EmcFolderChangeDao {

    @Autowired
    private EmcFolderChangeMapper emcFolderMapper;

    @Override
    public EmcFolderChangeMapper getMapper() {
        return emcFolderMapper;
    }

}
