package com.sunwayworld.emc.module.mdm.bizmdm.indexcategory.dao.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.sunwayworld.emc.module.mdm.bizmdm.indexcategory.bean.EmcIndexPreviewBean;
import com.sunwayworld.emc.module.mdm.bizmdm.indexcategory.dao.EmcIndexPreviewDao;
import com.sunwayworld.emc.module.mdm.bizmdm.indexcategory.mapper.EmcIndexPreviewMapper;
import com.sunwayworld.framework.mybatis.dao.MybatisDaoSupport;

/**
 * @author guok@sunwayworld.com 2020/02/05
 */

@Repository
public class EmcIndexPreviewDaoImpl extends MybatisDaoSupport<EmcIndexPreviewBean, Long>implements EmcIndexPreviewDao {

    @Autowired
    private EmcIndexPreviewMapper indexPreviewMapper;

    @Override
    public EmcIndexPreviewMapper getMapper() {
        return indexPreviewMapper;
    }
}
