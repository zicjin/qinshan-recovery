package com.sunwayworld.emc.module.mdm.bizmdm.test.service.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.sunwayworld.emc.framework.constant.EmcConstant;
import com.sunwayworld.emc.module.mdm.bizmdm.test.bean.EmcItemBean;
import com.sunwayworld.emc.module.mdm.bizmdm.test.bean.EmcItemRevisionBean;
import com.sunwayworld.emc.module.mdm.bizmdm.test.dao.EmcItemRevisionDao;
import com.sunwayworld.emc.module.mdm.bizmdm.test.service.EmcItemRevisionService;
import com.sunwayworld.framework.at.annotation.AuditTrailEntry;
import com.sunwayworld.framework.at.annotation.AuditTrailType;
import com.sunwayworld.framework.context.ApplicationContextHelper;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.utils.BeanUtils;

/**
 * @author yuanhh@sunwayworld.com
 * @date 2019-09-19
 */

@Repository
public class EmcItemRevisionServiceImpl implements EmcItemRevisionService {
    @Autowired
    private EmcItemRevisionDao emcItemRevisionDao;

    @Override
    @SuppressWarnings("unchecked")
    public EmcItemRevisionDao getDao() {
        return emcItemRevisionDao;
    }

    @Override
    @Transactional
    @AuditTrailEntry(AuditTrailType.INSERT)
    public Long insert(Long itemId, RestJsonWrapperBean jsonWrapper) {
        EmcItemRevisionBean emcItemRevision = jsonWrapper.parseUnique(EmcItemRevisionBean.class);
        emcItemRevision.setId(ApplicationContextHelper.getNextIdentity());
        emcItemRevision.setItemId(itemId);
        getDao().insert(emcItemRevision);
        return emcItemRevision.getId();
    }

    @Override
    @Transactional
    @AuditTrailEntry(AuditTrailType.INSERT)
    public void copyItemRevision(RestJsonWrapperBean wrapper) {
        List<EmcItemRevisionBean> itemRevisionList = wrapper.parse(EmcItemRevisionBean.class);
        List<EmcItemRevisionBean> newItemRevisionList = new ArrayList<>();

        Collections.reverse(itemRevisionList);
        itemRevisionList.forEach(r -> {
            EmcItemRevisionBean newRevision = new EmcItemRevisionBean();
            BeanUtils.copyProperties(r, newRevision, EmcConstant.CREATED_COLUMNS);
            newRevision.setId(ApplicationContextHelper.getNextIdentity());
            newItemRevisionList.add(newRevision);
        });

        getDao().insert(newItemRevisionList);
    }

    @Override
    @Transactional
    @AuditTrailEntry(AuditTrailType.INSERT)
    public void copyItemRevisionTo(RestJsonWrapperBean wrapper) {
        List<EmcItemRevisionBean> itemRevisionList = wrapper.parse(EmcItemRevisionBean.class);
        List<EmcItemBean> itemList = wrapper.parse(EmcItemBean.class);

        Collections.reverse(itemRevisionList);
        List<EmcItemRevisionBean> newItemRevisionList = new ArrayList<>();
        itemRevisionList.forEach(r -> {
            itemList.forEach(i -> {
                EmcItemRevisionBean newRevision = new EmcItemRevisionBean();
                BeanUtils.copyProperties(r, newRevision, EmcConstant.CREATED_COLUMNS);
                newRevision.setId(ApplicationContextHelper.getNextIdentity());
                newRevision.setItemId(i.getId());
                newItemRevisionList.add(newRevision);
            });
        });

        getDao().insert(newItemRevisionList);
    }

}
