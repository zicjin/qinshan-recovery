/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.script.result.service.impl;

import com.sunwayworld.cloud.module.script.computing.CoreResultComputingEngine;
import com.sunwayworld.cloud.module.script.result.persistent.dao.CoreResultDao;
import com.sunwayworld.cloud.module.script.result.service.CoreResultService;
import com.sunwayworld.framework.audit.aunnotation.Audit;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@GikamBean
public class CoreResultServiceImpl
implements CoreResultService {
    @Autowired
    private CoreResultDao coreResultDao;

    @Override
    public CoreResultDao getDao() {
        return this.coreResultDao;
    }

    @Override
    @Transactional
    @Audit(value="AUDIT.SAVE")
    public void compute(RestJsonWrapperBean wrapper) {
        List<Long> idList = wrapper.parseId(Long.class);
        CoreResultComputingEngine.getEngine(idList).compute();
    }
}
