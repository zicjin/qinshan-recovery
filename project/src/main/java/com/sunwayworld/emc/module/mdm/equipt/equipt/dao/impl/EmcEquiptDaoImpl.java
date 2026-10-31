package com.sunwayworld.emc.module.mdm.equipt.equipt.dao.impl;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.sunwayworld.emc.module.mdm.equipt.equipt.bean.EmcEquiptBean;
import com.sunwayworld.emc.module.mdm.equipt.equipt.dao.EmcEquiptDao;
import com.sunwayworld.emc.module.mdm.equipt.equipt.mapper.EmcEquiptMapper;
import com.sunwayworld.framework.mybatis.dao.MybatisDaoSupport;

@Repository
public class EmcEquiptDaoImpl extends MybatisDaoSupport<EmcEquiptBean, Long> implements EmcEquiptDao{

    @Autowired
    private EmcEquiptMapper emcEquiptMapper;

    @Override
    public EmcEquiptMapper getMapper() {
        return emcEquiptMapper;
    }

    @Override
    public List<EmcEquiptBean> selectByConditionByTestMethodIdList(List<String> testMethodIds) {
        return emcEquiptMapper.selectByConditionByTestMethodIdList(testMethodIds);
    }

    @Override
    public List<Map<String, Object>> selectCaliChoose() {
        return getMapper().selectCaliChoose();
    }

    @Override
    public List<Map<String, Object>> selectVerifyChoose() {
        return getMapper().selectVerifyChoose();
    }

    @Override
    public List<Map<String, Object>> selectPerCheckChoose() {
        return getMapper().selectPerCheckChoose();
    }

    @Override
    public List<EmcEquiptBean> selectMaxEquiptIdByEquiptCategoryId(long equiptCategoryId) {
        return getMapper().selectMaxEquiptIdByEquiptCategoryId(equiptCategoryId);
    }

}
