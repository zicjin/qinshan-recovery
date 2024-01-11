package com.sunwayworld.emc.module.mdm.bizmdm.indexcategory.dao.impl;

import com.sunwayworld.framework.spring.annotation.GikamBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.sunwayworld.emc.module.mdm.bizmdm.indexcategory.bean.EmcOrderIndexBean;
import com.sunwayworld.emc.module.mdm.bizmdm.indexcategory.dao.EmcOrderIndexDao;
import com.sunwayworld.emc.module.mdm.bizmdm.indexcategory.mapper.EmcOrderIndexMapper;
import com.sunwayworld.framework.mybatis.dao.MybatisDaoSupport;

/**
 * @author zhangsy@sunwayworld.com 2019年9月17日
 */
@Repository
@GikamBean
public class EmcOrderIndexDaoImpl extends MybatisDaoSupport<EmcOrderIndexBean, String> implements EmcOrderIndexDao {

    @Autowired
    private EmcOrderIndexMapper emcOrderIndexMapper;

    @Override
    public EmcOrderIndexMapper getMapper() {
        return emcOrderIndexMapper;
    }

}
