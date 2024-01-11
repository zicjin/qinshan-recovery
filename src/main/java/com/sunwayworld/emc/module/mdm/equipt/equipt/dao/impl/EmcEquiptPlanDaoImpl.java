package com.sunwayworld.emc.module.mdm.equipt.equipt.dao.impl;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.sunwayworld.emc.module.mdm.equipt.equipt.bean.EmcEquiptPlanBean;
import com.sunwayworld.emc.module.mdm.equipt.equipt.dao.EmcEquiptPlanDao;
import com.sunwayworld.emc.module.mdm.equipt.equipt.mapper.EmcEquiptPlanMapper;
import com.sunwayworld.framework.mybatis.dao.MybatisDaoSupport;
import com.sunwayworld.framework.mybatis.mapper.MapperParameter;


/**
 * @author cuikx@sunwayworld.com
 * @date 2019-11-21
 */
@Repository
public class EmcEquiptPlanDaoImpl extends MybatisDaoSupport<EmcEquiptPlanBean, Long>implements EmcEquiptPlanDao {
    
    @Autowired
    private EmcEquiptPlanMapper emcEquiptPlanMapper;
    
    @Override
    public EmcEquiptPlanMapper getMapper() {
        return emcEquiptPlanMapper;
    }
    
    
    @Override
    public List<Map<String, Object>> selectEquiptMaintPlanOverdue(MapperParameter parameter) {
        return getMapper().selectEquiptMaintPlanOverdue(parameter);
    }

    @Override
    public void updatePlan(EmcEquiptPlanBean emcEquiptPlanBean) {
        getMapper().updatePlan(emcEquiptPlanBean);
    }
}
