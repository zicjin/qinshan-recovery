package com.sunwayworld.emc.module.mdm.bizmdm.indexcategory.dao.impl;

import com.sunwayworld.framework.mybatis.mapper.MapperParameter;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.sunwayworld.emc.module.mdm.bizmdm.indexcategory.bean.EmcIndexSetBean;
import com.sunwayworld.emc.module.mdm.bizmdm.indexcategory.dao.EmcIndexSetDao;
import com.sunwayworld.emc.module.mdm.bizmdm.indexcategory.mapper.EmcIndexSetMapper;
import com.sunwayworld.framework.mybatis.dao.MybatisDaoSupport;

import java.util.List;

@Repository
@GikamBean
public class EmcIndexSetDaoImpl extends MybatisDaoSupport<EmcIndexSetBean, Long> implements EmcIndexSetDao {

    @Autowired
    private EmcIndexSetMapper emcIndexSetMapper;

    @Override
    public EmcIndexSetMapper getMapper() {
        return emcIndexSetMapper;
    }

    @Override
    public List<EmcIndexSetBean> selectListByIndexIdAndItemId(Long indexId, Long itemId) {
        MapperParameter parameter = new MapperParameter();
        parameter.put("indexId", indexId);
        parameter.put("itemId", itemId);
        return getMapper().selectListByIndexIdAndItemId(parameter);
    }

}
