/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.module.sys.attorney.service.impl;

import com.sunwayworld.framework.database.sql.Order;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import com.sunwayworld.module.sys.attorney.bean.CoreAttorneyMenuBean;
import com.sunwayworld.module.sys.attorney.dao.CoreAttorneyMenuDao;
import com.sunwayworld.module.sys.attorney.service.CoreAttorneyMenuService;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Repository;

@GikamBean
@Repository
public class CoreAttorneyMenuServiceImpl
implements CoreAttorneyMenuService {
    @Autowired
    private CoreAttorneyMenuDao attorneyMenuDao;

    @Override
    public CoreAttorneyMenuDao getDao() {
        return this.attorneyMenuDao;
    }

    @Override
    @Cacheable(value={"T_CORE_ATTORNEY_MENU.BY_ATTORNEYID"}, key="#attorneyId", unless="#result == null")
    public List<CoreAttorneyMenuBean> selectListByAttoryneyId(Long attorneyId) {
        CoreAttorneyMenuBean filter = new CoreAttorneyMenuBean();
        filter.setAttorneyId(attorneyId);
        return this.getDao().selectList(filter, new Order[0]);
    }
}
