package com.sunwayworld.emc.module.bp.reagent.dao.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.sunwayworld.emc.module.bp.reagent.bean.EmcReagentCompositionBean;
import com.sunwayworld.emc.module.bp.reagent.dao.EmcReagentCompositionDao;
import com.sunwayworld.emc.module.bp.reagent.mapper.EmcReagentCompositionMapper;
import com.sunwayworld.framework.mybatis.dao.MybatisDaoSupport;

/**
* @author zhengxy@sunwayworld.com 2020年06月18日
*/
@Repository
public class EmcReagentCompositionDaoImpl extends MybatisDaoSupport<EmcReagentCompositionBean, Long> implements EmcReagentCompositionDao {

    @Autowired
    private EmcReagentCompositionMapper reagentCompositionMapper;

    @Override
    public EmcReagentCompositionMapper getMapper() {
        return reagentCompositionMapper;
    }
}
