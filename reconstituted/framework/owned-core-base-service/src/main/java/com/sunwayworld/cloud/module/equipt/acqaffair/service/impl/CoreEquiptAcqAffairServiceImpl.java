/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.equipt.acqaffair.service.impl;

import com.sunwayworld.cloud.module.equipt.acqaffair.persistent.dao.CoreEquiptAcqAffairDao;
import com.sunwayworld.cloud.module.equipt.acqaffair.service.CoreEquiptAcqAffairService;
import com.sunwayworld.framework.exception.NotImplementedException;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import java.io.File;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
@GikamBean
public class CoreEquiptAcqAffairServiceImpl
implements CoreEquiptAcqAffairService {
    @Autowired
    private CoreEquiptAcqAffairDao coreEquiptAcqAffairDao;

    @Override
    public CoreEquiptAcqAffairDao getDao() {
        return this.coreEquiptAcqAffairDao;
    }

    @Override
    public void insertByFile(File file) {
        throw new NotImplementedException();
    }
}
