/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.item.chem.service.impl;

import com.sunwayworld.cloud.module.item.chem.bean.CoreChemBean;
import com.sunwayworld.cloud.module.item.chem.persistent.dao.CoreChemDao;
import com.sunwayworld.cloud.module.item.chem.service.CoreChemService;
import com.sunwayworld.framework.context.ApplicationContextHelper;
import com.sunwayworld.framework.database.sql.Order;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@GikamBean
public class CoreChemServiceImpl
implements CoreChemService {
    @Autowired
    private CoreChemDao chemDao;

    @Override
    public CoreChemDao getDao() {
        return this.chemDao;
    }

    @Override
    @Transactional
    public Long insert(RestJsonWrapperBean wrapper) {
        CoreChemBean chem = wrapper.parseUnique(CoreChemBean.class);
        chem.setId(ApplicationContextHelper.getNextIdentity());
        this.getDao().insert(chem);
        return chem.getId();
    }

    @Override
    public CoreChemBean selectUniqueByTargetId(String targetId) {
        CoreChemBean filter = new CoreChemBean();
        filter.setTargetId(targetId);
        return this.getDao().selectFirstIfPresent(filter, new Order[0]);
    }
}
