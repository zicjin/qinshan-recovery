package com.sunwayworld.emc.module.mdm.bizmdm.client.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.sunwayworld.emc.framework.constant.EmcConstant;
import com.sunwayworld.emc.module.mdm.bizmdm.client.bean.EmcMonitorPointBean;
import com.sunwayworld.emc.module.mdm.bizmdm.client.bean.EmcMonitorPointSchemeBean;
import com.sunwayworld.emc.module.mdm.bizmdm.client.dao.EmcMonitorPointSchemeDao;
import com.sunwayworld.emc.module.mdm.bizmdm.client.service.EmcMonitorPointSchemeService;
import com.sunwayworld.framework.at.annotation.AuditTrailEntry;
import com.sunwayworld.framework.at.annotation.AuditTrailType;
import com.sunwayworld.framework.context.ApplicationContextHelper;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.utils.BeanUtils;
import com.sunwayworld.framework.utils.NumberUtils;
import com.sunwayworld.framework.utils.ObjectUtils;

/**
 * @author magh@sunwayworld.com
 * @date 2019-10-19
 */

@Repository
public class EmcMonitorPointSchemeServiceImpl implements EmcMonitorPointSchemeService {
    @Autowired
    private EmcMonitorPointSchemeDao emcMonitorPointSchemeDao;

    @Override
    @SuppressWarnings("unchecked")
    public EmcMonitorPointSchemeDao getDao() {
        return emcMonitorPointSchemeDao;
    }

    @Override
    @Transactional
    @AuditTrailEntry(AuditTrailType.INSERT)
    public void insert(Long monitorPointId, RestJsonWrapperBean jsonWrapper) {
        List<EmcMonitorPointSchemeBean> monitorPointSchemeList = jsonWrapper.parse(EmcMonitorPointSchemeBean.class);

        List<EmcMonitorPointSchemeBean> insertMonitorPointSchemeList = new ArrayList<>();
        monitorPointSchemeList.stream().forEach(e -> {
            EmcMonitorPointSchemeBean monitorPointScheme = new EmcMonitorPointSchemeBean();
            BeanUtils.copyProperties(e, monitorPointScheme);
            monitorPointScheme.setId(ApplicationContextHelper.getNextIdentity());
            monitorPointScheme.setMonitorPointId(monitorPointId);
            monitorPointScheme.setMonitorTmplGroupId(NumberUtils.parseLong(e.getExt$Item("monitortmplgroupid")));
            monitorPointScheme.setSchemeId(e.getId());
            monitorPointScheme.setSampleNum(EmcConstant.ONE);
            insertMonitorPointSchemeList.add(monitorPointScheme);
        });

        getDao().insert(insertMonitorPointSchemeList);
    }

    @Override
    @Transactional
    @AuditTrailEntry(AuditTrailType.INSERT)
    public void copyMonitorPointScheme(Long monitorPointId, RestJsonWrapperBean wrapper) {

        List<Long> targetMonitorPointIdList = wrapper.parse(EmcMonitorPointBean.class).stream().map(e -> NumberUtils.parseLong(e.getId())).collect(Collectors.toList());

        List<EmcMonitorPointSchemeBean> copyMonitorPointSchemeList = getDao().selectListByOneColumnValue(monitorPointId, "monitorPointId");

        List<EmcMonitorPointSchemeBean> oldMonitorPointSchemeList = getDao().selectListByOneColumnValues(targetMonitorPointIdList, "monitorPointId");

        List<EmcMonitorPointSchemeBean> insertMonitorPointSchemeList = new ArrayList<>();

        targetMonitorPointIdList.stream().forEach(targetMonitorPointId -> {

            List<Long> oldMonitorPointSchemeIdList = oldMonitorPointSchemeList.stream().filter(e -> ObjectUtils.equals(e.getMonitorPointId(), targetMonitorPointId)).map(e -> e.getSchemeId())
                    .collect(Collectors.toList());
            List<EmcMonitorPointSchemeBean> addMonitorPointSchemeList = ObjectUtils.clone(copyMonitorPointSchemeList).stream().filter(e -> !oldMonitorPointSchemeIdList.contains(e.getSchemeId()))
                    .collect(Collectors.toList());

            if (!addMonitorPointSchemeList.isEmpty()) {
                addMonitorPointSchemeList.forEach(e -> {
                    e.setId(ApplicationContextHelper.getNextIdentity());
                    e.setMonitorPointId(targetMonitorPointId);
                    insertMonitorPointSchemeList.add(e);
                });
            }
        });
        getDao().insert(insertMonitorPointSchemeList);
    }

}
