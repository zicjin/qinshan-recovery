package com.sunwayworld.emc.module.mdm.basemdm.datacorrelation.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.sunwayworld.emc.module.mdm.basemdm.datacorrelation.bean.EmcDataCorrelationItemBean;
import com.sunwayworld.emc.module.mdm.basemdm.datacorrelation.dao.EmcDataCorrelationItemDao;
import com.sunwayworld.emc.module.mdm.basemdm.datacorrelation.service.EmcDataCorrelationItemService;
import com.sunwayworld.framework.at.annotation.AuditTrailEntry;
import com.sunwayworld.framework.at.annotation.AuditTrailType;
import com.sunwayworld.framework.context.ApplicationContextHelper;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.spring.annotation.GikamBean;

/**
 * @author zhangft@sunwayworld.com@sunwayworld.com
 * @date 2020-01-03
 */

@Repository
@GikamBean
public class EmcDataCorrelationItemServiceImpl implements EmcDataCorrelationItemService {
    @Autowired
    protected EmcDataCorrelationItemDao dataCorrelationItemDao;

    @Override
    @SuppressWarnings("unchecked")
    public EmcDataCorrelationItemDao getDao() {
        return dataCorrelationItemDao;
    }

    /**
     * 插入单条数据
     */
    @Override
    @Transactional
    @AuditTrailEntry(AuditTrailType.INSERT)
    public Long insert(RestJsonWrapperBean jsonWrapper) {
        EmcDataCorrelationItemBean dataCorrelationItem = jsonWrapper.parseUnique(EmcDataCorrelationItemBean.class);
        dataCorrelationItem.setId(ApplicationContextHelper.getNextIdentity());
        getDao().insert(dataCorrelationItem);
        return dataCorrelationItem.getId();
    }

    /**
     * 插入多条数据
     */
    @Override
    @Transactional
    @AuditTrailEntry(AuditTrailType.INSERT)
    public void insertMul(RestJsonWrapperBean jsonWrapper) {
        List<EmcDataCorrelationItemBean> dataCorrelationItemList = jsonWrapper.parse(EmcDataCorrelationItemBean.class);
        dataCorrelationItemList.forEach(dataCorrelationItem -> {
            dataCorrelationItem.setId(ApplicationContextHelper.getNextIdentity());
        });
        getDao().insert(dataCorrelationItemList);
    }

}
