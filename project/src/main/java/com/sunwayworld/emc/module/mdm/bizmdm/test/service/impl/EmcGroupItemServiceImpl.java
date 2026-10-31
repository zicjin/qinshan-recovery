package com.sunwayworld.emc.module.mdm.bizmdm.test.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.sunwayworld.emc.module.mdm.bizmdm.test.bean.EmcGroupItemBean;
import com.sunwayworld.emc.module.mdm.bizmdm.test.dao.EmcGroupItemDao;
import com.sunwayworld.emc.module.mdm.bizmdm.test.service.EmcGroupItemService;
import com.sunwayworld.framework.at.annotation.AuditTrailEntry;
import com.sunwayworld.framework.at.annotation.AuditTrailType;
import com.sunwayworld.framework.context.ApplicationContextHelper;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;

/**
 * @author yuanhh@sunwayworld.com
 * @date 2019-09-19
 */

@Repository
public class EmcGroupItemServiceImpl implements EmcGroupItemService {
    @Autowired
    private EmcGroupItemDao emcGroupItemDao;

    @Override
    @SuppressWarnings("unchecked")
    public EmcGroupItemDao getDao() {
        return emcGroupItemDao;
    }

    @Override
    @Transactional
    @AuditTrailEntry(AuditTrailType.INSERT)
    public void insert(Long itemGroupId, RestJsonWrapperBean jsonWrapper) {
        List<EmcGroupItemBean> groupItemList = jsonWrapper.parse(EmcGroupItemBean.class);
        groupItemList.stream().forEach(e -> {
            e.setId(ApplicationContextHelper.getNextIdentity());
            e.setItemGroupId(itemGroupId);
        });
        getDao().insert(groupItemList);
    }

}
