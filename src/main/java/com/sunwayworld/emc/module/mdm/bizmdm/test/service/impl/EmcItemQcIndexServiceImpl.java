package com.sunwayworld.emc.module.mdm.bizmdm.test.service.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.sunwayworld.emc.module.mdm.bizmdm.test.bean.EmcItemBean;
import com.sunwayworld.emc.module.mdm.bizmdm.test.bean.EmcItemQcIndexBean;
import com.sunwayworld.emc.module.mdm.bizmdm.test.dao.EmcItemQcIndexDao;
import com.sunwayworld.emc.module.mdm.bizmdm.test.service.EmcItemQcIndexService;
import com.sunwayworld.emc.module.mdm.bizmdm.test.service.EmcItemService;
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
public class EmcItemQcIndexServiceImpl implements EmcItemQcIndexService {

    @Autowired
    @Lazy
    private EmcItemService itemService;

    @Autowired
    private EmcItemQcIndexDao emcItemQcIndexDao;

    @Override
    @SuppressWarnings("unchecked")
    public EmcItemQcIndexDao getDao() {
        return emcItemQcIndexDao;
    }

    @Override
    @Transactional
    @AuditTrailEntry(AuditTrailType.INSERT)
    public Long insert(Long itemId, RestJsonWrapperBean jsonWrapper) {
        List<EmcItemQcIndexBean> qcIndexList = jsonWrapper.parse(EmcItemQcIndexBean.class);
        Collections.reverse(qcIndexList);

        List<EmcItemQcIndexBean> newQcIndexList = new ArrayList<>();
        qcIndexList.forEach(e -> {
            e.setId(ApplicationContextHelper.getNextIdentity());
            e.setItemId(itemId);
            newQcIndexList.add(e);
        });

        getDao().insert(newQcIndexList);
        return qcIndexList.get(0).getId();
    }

    @Override
    @Transactional
    @AuditTrailEntry(AuditTrailType.INSERT)
    public void copyTo(RestJsonWrapperBean jsonWrapper) {
        List<EmcItemQcIndexBean> qcIndexList = jsonWrapper.parse(EmcItemQcIndexBean.class);
        List<EmcItemBean> itemList = jsonWrapper.parse(EmcItemBean.class);

        Collections.reverse(qcIndexList);
        List<EmcItemQcIndexBean> insertQcIndexList = new ArrayList<>();
        qcIndexList.forEach(q -> {

            itemList.forEach(i -> {
                    EmcItemQcIndexBean insertQcIndex = new EmcItemQcIndexBean();
                    BeanUtils.copyProperties(q, insertQcIndex);
                    insertQcIndex.setId(ApplicationContextHelper.getNextIdentity());
                    insertQcIndex.setItemId(i.getId());
                    insertQcIndexList.add(insertQcIndex);
                });

        });

        getDao().insert(insertQcIndexList);
    }

}
