package com.sunwayworld.emc.module.mdm.bizmdm.client.service.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.sunwayworld.emc.module.mdm.basemdm.monitorpointcategory.bean.EmcMonitorPointCategoryBean;
import com.sunwayworld.emc.module.mdm.basemdm.monitorpointcategory.service.EmcMonitorPointCategoryService;
import com.sunwayworld.emc.module.mdm.basemdm.propertytmpl.bean.EmcPropertyDataBean;
import com.sunwayworld.emc.module.mdm.basemdm.propertytmpl.service.EmcPropertyDataService;
import com.sunwayworld.emc.module.mdm.bizmdm.client.bean.EmcMonitorPointBean;
import com.sunwayworld.emc.module.mdm.bizmdm.client.bean.EmcMonitorPointSchemeBean;
import com.sunwayworld.emc.module.mdm.bizmdm.client.dao.EmcMonitorPointDao;
import com.sunwayworld.emc.module.mdm.bizmdm.client.service.EmcMonitorPointSchemeService;
import com.sunwayworld.emc.module.mdm.bizmdm.client.service.EmcMonitorPointService;
import com.sunwayworld.framework.at.annotation.AuditTrailEntry;
import com.sunwayworld.framework.at.annotation.AuditTrailType;
import com.sunwayworld.framework.context.ApplicationContextHelper;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.utils.BeanUtils;
import com.sunwayworld.framework.utils.ObjectUtils;

/**
 * @author magh@sunwayworld.com
 * @date 2019-10-19
 */

@Repository
public class EmcMonitorPointServiceImpl implements EmcMonitorPointService {

    @Autowired
    private EmcMonitorPointDao emcMonitorPointDao;

    @Autowired
    @Lazy
    private EmcMonitorPointCategoryService monitorPointCategoryService;

    @Autowired
    @Lazy
    private EmcPropertyDataService propertyDataService;

    @Autowired
    @Lazy
    private EmcMonitorPointSchemeService monitorPointSchemeService;

    @Override
    @SuppressWarnings("unchecked")
    public EmcMonitorPointDao getDao() {
        return emcMonitorPointDao;
    }

    @Override
    @Transactional
    @AuditTrailEntry(AuditTrailType.INSERT)
    public void insert(Long clientId, RestJsonWrapperBean jsonWrapper) {
        EmcMonitorPointBean emcMonitorPoint = jsonWrapper.parseUnique(EmcMonitorPointBean.class);
        emcMonitorPoint.setId(ApplicationContextHelper.getNextIdentity());
        emcMonitorPoint.setClientId(clientId);

        // 按已存在的最大排序号自增
        List<EmcMonitorPointBean> existMonitorPointList = getDao().selectListByOneColumnValue(clientId, "CLIENTID", Arrays.asList("ID", "ORDERNO"));
        Long existedMaxOrderNo = existMonitorPointList.stream().filter(point -> !ObjectUtils.isEmpty(point.getOrderNo())).map(EmcMonitorPointBean::getOrderNo).max(Comparator.comparing(Function.identity())).orElse(1L);
        emcMonitorPoint.setOrderNo(existedMaxOrderNo + 1);

        getDao().insert(emcMonitorPoint);

        EmcPropertyDataBean propertyDataBean = new EmcPropertyDataBean();
        propertyDataBean.setBizId(emcMonitorPoint.getId());
        EmcMonitorPointCategoryBean monitorCategory = monitorPointCategoryService.selectById(emcMonitorPoint.getMonitorPointCategoryId());
        propertyDataBean.setTmplId(monitorCategory.getMonitorPointType());
        List<EmcPropertyDataBean> dataList = propertyDataService.getDao().selectList(propertyDataBean);
        if (dataList.isEmpty()) {
            propertyDataBean.setId(ApplicationContextHelper.getNextIdentity());
            propertyDataService.getDao().insert(propertyDataBean);
        }
    }

    @Override
    @Transactional
    @AuditTrailEntry(AuditTrailType.INSERT)
    public void copy(RestJsonWrapperBean wrapper) {

        List<EmcMonitorPointBean> copyMonitorPointList = wrapper.parse(EmcMonitorPointBean.class);
        List<Long> copyMonitorPointIdList = copyMonitorPointList.stream().map(e -> e.getId()).collect(Collectors.toList());
        List<EmcMonitorPointSchemeBean> copyMonitorPointSchemeList = monitorPointSchemeService.getDao().selectListByOneColumnValues(copyMonitorPointIdList, "MONITORPOINTID");
        List<EmcPropertyDataBean> copyPropertyDataList = propertyDataService.getDao().selectListByOneColumnValues(copyMonitorPointIdList, "BIZID");

        List<EmcMonitorPointBean> insertMonitorPointList = new ArrayList<>();
        List<EmcMonitorPointSchemeBean> insertMonitorPointSchemeList = new ArrayList<>();
        List<EmcPropertyDataBean> insertPropertyDataList = new ArrayList<>();

        int copyNum = copyMonitorPointIdList.size();
        List<Long> insertMonitorPointIdList = ApplicationContextHelper.getNextIdentityList(copyNum);

        for (int i = 0; i < copyNum; i++) {
            EmcMonitorPointBean copyMonitorPoint = copyMonitorPointList.get(i);
            Long copyId = copyMonitorPoint.getId();
            Long insertId = insertMonitorPointIdList.get(i);
            EmcMonitorPointBean insertMonitorPoint = new EmcMonitorPointBean();
            BeanUtils.copyProperties(copyMonitorPoint, insertMonitorPoint);
            insertMonitorPoint.setId(insertId);
            insertMonitorPointList.add(insertMonitorPoint);

            if (!copyMonitorPointSchemeList.isEmpty()) {
                List<EmcMonitorPointSchemeBean> copySchemeOfMonitorPointList = copyMonitorPointSchemeList.stream().filter(e -> ObjectUtils.equals(copyId, e.getMonitorPointId()))
                        .collect(Collectors.toList());
                if (!copySchemeOfMonitorPointList.isEmpty()) {
                    copySchemeOfMonitorPointList.forEach(e -> {
                        EmcMonitorPointSchemeBean insertMonitorPointScheme = new EmcMonitorPointSchemeBean();
                        BeanUtils.copyProperties(e, insertMonitorPointScheme);
                        insertMonitorPointScheme.setId(ApplicationContextHelper.getNextIdentity());
                        insertMonitorPointScheme.setMonitorPointId(insertId);
                        insertMonitorPointSchemeList.add(insertMonitorPointScheme);
                    });
                }
            }

            if (!copyPropertyDataList.isEmpty()) {
                EmcPropertyDataBean copyPropertyDataOfMonitorPoint = copyPropertyDataList.stream().filter(e -> ObjectUtils.equals(copyId, e.getBizId())).findFirst().orElse(null);
                EmcPropertyDataBean insertPropertyData = new EmcPropertyDataBean();
                if (copyPropertyDataOfMonitorPoint != null) {
                    BeanUtils.copyProperties(copyPropertyDataOfMonitorPoint, insertPropertyData);
                }
                insertPropertyData.setId(ApplicationContextHelper.getNextIdentity());
                insertPropertyData.setBizId(insertId);
                insertPropertyDataList.add(insertPropertyData);
            }
        }

        getDao().insert(insertMonitorPointList);
        monitorPointSchemeService.getDao().insert(insertMonitorPointSchemeList);
        propertyDataService.getDao().insert(insertPropertyDataList);

    }
    @Override
    @Transactional
    @AuditTrailEntry(AuditTrailType.DELETE)
    public void delete(RestJsonWrapperBean wrapper) {
        List<EmcMonitorPointBean> monitorPointList = wrapper.parse(EmcMonitorPointBean.class);
        List<Long> idList = monitorPointList.stream().map(a -> a.getId()).collect(Collectors.toList());
        List<EmcPropertyDataBean> dataList = propertyDataService.getDao().selectListByOneColumnValues(idList, "BIZID");
        propertyDataService.getDao().deleteBy(dataList, "ID");
        getDao().deleteByIdList(idList);
    }
}
