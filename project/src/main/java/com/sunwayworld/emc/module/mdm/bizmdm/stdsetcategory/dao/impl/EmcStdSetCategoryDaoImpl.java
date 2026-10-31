package com.sunwayworld.emc.module.mdm.bizmdm.stdsetcategory.dao.impl;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.sunwayworld.emc.module.mdm.bizmdm.stdsetcategory.bean.EmcStdSetCategoryBean;
import com.sunwayworld.emc.module.mdm.bizmdm.stdsetcategory.dao.EmcStdSetCategoryDao;
import com.sunwayworld.emc.module.mdm.bizmdm.stdsetcategory.mapper.EmcStdSetCategoryMapper;
import com.sunwayworld.framework.mybatis.dao.MybatisDaoSupport;
import com.sunwayworld.framework.mybatis.mapper.MapperParameter;

@Repository
public class EmcStdSetCategoryDaoImpl extends MybatisDaoSupport<EmcStdSetCategoryBean, Long>implements EmcStdSetCategoryDao {

    @Autowired
    private EmcStdSetCategoryMapper emcStdSetCategoryMapper;

    @Override
    public EmcStdSetCategoryMapper getMapper() {
        return emcStdSetCategoryMapper;
    }

    @Override
    public List<Map<String, Object>> selectFieldInfoPagination(MapperParameter mapperParameter) {
        return emcStdSetCategoryMapper.selectFieldInfoPagination(mapperParameter);
    }

}
