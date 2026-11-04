/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.module.sys.log.service.impl;

import com.sunwayworld.framework.context.ApplicationContextHelper;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import com.sunwayworld.module.sys.log.bean.CoreLogBean;
import com.sunwayworld.module.sys.log.dao.CoreLogDao;
import com.sunwayworld.module.sys.log.service.CoreLogService;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@GikamBean
@Repository
public class CoreLogServiceImpl
implements CoreLogService {
    @Autowired
    private CoreLogDao logDao;

    @Override
    public CoreLogDao getDao() {
        return this.logDao;
    }

    @Override
    @Transactional
    public void insertLog(List<CoreLogBean> insertSysLogList) {
        List<Long> keyList = ApplicationContextHelper.getNextIdentityList(insertSysLogList.size());
        int j = insertSysLogList.size();
        for (int i = 0; i < j; ++i) {
            insertSysLogList.get(i).setId(keyList.get(i));
        }
        this.getDao().insert(insertSysLogList);
    }
}
