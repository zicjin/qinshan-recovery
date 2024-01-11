package com.sunwayworld.emc.module.mdm.bizmdm.capacity.service.impl;

import com.sunwayworld.emc.module.mdm.bizmdm.capacity.bean.EmcCapacityItemGroupBean;
import com.sunwayworld.emc.module.mdm.bizmdm.capacity.dao.EmcCapacityItemGroupDao;
import com.sunwayworld.emc.module.mdm.bizmdm.capacity.service.EmcCapacityItemGroupService;
import com.sunwayworld.framework.at.annotation.AuditTrailEntry;
import com.sunwayworld.framework.at.annotation.AuditTrailType;
import com.sunwayworld.framework.context.ApplicationContextHelper;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @author luoc@sunwayworld.com
 * @date 2021-04-15
 */

@Repository
public class EmcCapacityItemGroupServiceImpl implements EmcCapacityItemGroupService {


    @Autowired
    private EmcCapacityItemGroupDao capacityItemGroupDao;

    @Override
    @SuppressWarnings("unchecked")
    public EmcCapacityItemGroupDao getDao() {
        return capacityItemGroupDao;
    }

    @Override
    @Transactional
    @AuditTrailEntry(AuditTrailType.INSERT)
    public Long batchInsert(Long id,RestJsonWrapperBean jsonWrapper) {
        List<EmcCapacityItemGroupBean> itemGroupList = jsonWrapper.parse(EmcCapacityItemGroupBean.class);
        itemGroupList.forEach(e->{
            e.setId(ApplicationContextHelper.getNextIdentity());
            e.setTestCapacityId(id);
        });
        capacityItemGroupDao.insert(itemGroupList);
        return itemGroupList.get(0).getId();
    }
}
