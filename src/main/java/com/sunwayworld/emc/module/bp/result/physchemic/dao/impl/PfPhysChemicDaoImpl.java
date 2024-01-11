package com.sunwayworld.emc.module.bp.result.physchemic.dao.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.sunwayworld.emc.module.bp.result.physchemic.bean.PfPhysChemicBean;
import com.sunwayworld.emc.module.bp.result.physchemic.dao.PfPhysChemicDao;
import com.sunwayworld.emc.module.bp.result.physchemic.mapper.PfPhysChemicMapper;
import com.sunwayworld.framework.mybatis.dao.MybatisDaoSupport;
import com.sunwayworld.framework.spring.annotation.GikamBean;
/**
 * @author licm@sunwayworld.com 2022年07月12日
 */
@Repository
@GikamBean
public class PfPhysChemicDaoImpl extends MybatisDaoSupport<PfPhysChemicBean, Long> implements PfPhysChemicDao {

    @Autowired
    private PfPhysChemicMapper pfPhysChemicMapper;

    @Override
    public PfPhysChemicMapper getMapper() {
        return pfPhysChemicMapper;
    }

}
