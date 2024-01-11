package com.sunwayworld.emc.module.mdm.equipt.equiptacqresult.dao.impl;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.sunwayworld.emc.module.mdm.equipt.equiptacqresult.bean.EmcEquiptAcqResultBean;
import com.sunwayworld.emc.module.mdm.equipt.equiptacqresult.dao.EmcEquiptAcqResultDao;
import com.sunwayworld.emc.module.mdm.equipt.equiptacqresult.mapper.EmcEquiptAcqResultMapper;
import com.sunwayworld.framework.mybatis.dao.MybatisDaoSupport;
import com.sunwayworld.framework.spring.annotation.GikamBean;

@Repository
@GikamBean
public class EmcEquiptAcqResultDaoImpl extends MybatisDaoSupport<EmcEquiptAcqResultBean, Long> implements EmcEquiptAcqResultDao {

    @Autowired
    private EmcEquiptAcqResultMapper emcEquiptAcqResultMapper;

    @Override
    public EmcEquiptAcqResultMapper getMapper() {
        return emcEquiptAcqResultMapper;
    }

    @Override
    public List<Map<String, Object>> selectByConditionByBizNoList(List<String> bizNoList, String testId) {
        return getMapper().selectByConditionByBizNoList(bizNoList, testId);
    }

    @Override
    public List<Map<String, Object>> selectByConditionForAcqAutoUpdate(List<Long> idList) {
        return getMapper().selectByConditionForAcqAutoUpdate(idList);
    }

}
