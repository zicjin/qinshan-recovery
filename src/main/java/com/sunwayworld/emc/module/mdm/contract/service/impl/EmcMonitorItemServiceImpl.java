package com.sunwayworld.emc.module.mdm.contract.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.sunwayworld.emc.module.mdm.contract.bean.EmcMonitorItemBean;
import com.sunwayworld.emc.module.mdm.contract.dao.EmcMonitorItemDao;
import com.sunwayworld.emc.module.mdm.contract.service.EmcMonitorItemService;
import com.sunwayworld.framework.at.annotation.AuditTrailEntry;
import com.sunwayworld.framework.at.annotation.AuditTrailType;
import com.sunwayworld.framework.context.ApplicationContextHelper;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.spring.annotation.GikamBean;

@Repository
@GikamBean
public class EmcMonitorItemServiceImpl implements EmcMonitorItemService {

    @Autowired
    private EmcMonitorItemDao emcMonitorItemDao;

    @Override
    @SuppressWarnings("unchecked")
    public EmcMonitorItemDao getDao() {
        return emcMonitorItemDao;
    }

    @Override
    @Transactional
    @AuditTrailEntry(AuditTrailType.INSERT)
    public Long insert(RestJsonWrapperBean jsonWrapper) {
        List<EmcMonitorItemBean> emcMonitorItemList = jsonWrapper.parse(EmcMonitorItemBean.class);
        emcMonitorItemList.forEach(e -> {
            e.setId(ApplicationContextHelper.getNextIdentity());
            e.setExt$Item("folderNum", "1");
            e.setExt$Item("frequency", "1");
            e.setExt$Item("sampleNum", "1");
        });

        getDao().insert(emcMonitorItemList);
        return emcMonitorItemList.get(0).getId();
    }

}
