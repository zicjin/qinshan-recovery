package com.sunwayworld.emc.module.bp.reagent.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.sunwayworld.emc.module.bp.reagent.bean.EmcReagentCompositionBean;
import com.sunwayworld.emc.module.bp.reagent.dao.EmcReagentCompositionDao;
import com.sunwayworld.emc.module.bp.reagent.service.EmcReagentCompositionService;
import com.sunwayworld.framework.at.annotation.AuditTrailEntry;
import com.sunwayworld.framework.at.annotation.AuditTrailType;
import com.sunwayworld.framework.context.ApplicationContextHelper;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;

/**
* @author zhengxy@sunwayworld.com 2020年06月18日
*/
@Repository
public class EmcReagentCompositionServiceImpl implements EmcReagentCompositionService {

    @Autowired
    private EmcReagentCompositionDao reagentCompositionDao;

    @Override
    @SuppressWarnings("unchecked")
    public EmcReagentCompositionDao getDao() {
        return reagentCompositionDao;
    }

    @Override
    @Transactional
    @AuditTrailEntry(AuditTrailType.INSERT)
    public Long insert(Long reagentId, RestJsonWrapperBean wrapper) {
        EmcReagentCompositionBean composition = wrapper.parseUnique(EmcReagentCompositionBean.class);
        Long id = ApplicationContextHelper.getNextIdentity();
        composition.setId(id);
        composition.setReagentId(reagentId);
        getDao().insert(composition);
        return id;
    }
}
