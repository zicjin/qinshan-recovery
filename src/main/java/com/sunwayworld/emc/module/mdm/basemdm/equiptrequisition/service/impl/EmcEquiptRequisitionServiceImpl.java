package com.sunwayworld.emc.module.mdm.basemdm.equiptrequisition.service.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import com.sunwayworld.framework.context.LocalContextHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.sunwayworld.emc.module.mdm.basemdm.equiptrequisition.bean.EmcEquiptRequisitionBean;
import com.sunwayworld.emc.module.mdm.basemdm.equiptrequisition.dao.EmcEquiptRequisitionDao;
import com.sunwayworld.emc.module.mdm.basemdm.equiptrequisition.service.EmcEquiptRequisitionService;
import com.sunwayworld.emc.module.mdm.equipt.equipt.service.EmcEquiptService;
import com.sunwayworld.framework.at.annotation.AuditTrailEntry;
import com.sunwayworld.framework.at.annotation.AuditTrailType;
import com.sunwayworld.framework.constant.Constant;
import com.sunwayworld.framework.context.ApplicationContextHelper;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import com.sunwayworld.framework.utils.BeanUtils;
import com.sunwayworld.framework.utils.NumberUtils;
import com.sunwayworld.framework.utils.StringUtils;

@Repository
@GikamBean
public class EmcEquiptRequisitionServiceImpl implements EmcEquiptRequisitionService {

    @Autowired
    private EmcEquiptRequisitionDao emcEquiptRequisitionDao;

    @Autowired
    private EmcEquiptService emcEquiptService;

    @Override
    @SuppressWarnings("unchecked")
    public EmcEquiptRequisitionDao getDao() {
        return emcEquiptRequisitionDao;
    }

    @Override
    @Transactional
    @AuditTrailEntry(AuditTrailType.INSERT)
    public Long insert(RestJsonWrapperBean wrapper) {
        EmcEquiptRequisitionBean emcEquiptRequisition = wrapper.parseUnique(EmcEquiptRequisitionBean.class);
        String mulitEquiptIds = emcEquiptRequisition.getExt$Item("mulitequiptids");
        String[] equiptIds = StringUtils.split(mulitEquiptIds, ",");
        List<Long> validEquiptIds = Arrays.stream(equiptIds).filter(NumberUtils::isNumber).map(NumberUtils::parseLong).collect(Collectors.toList());

        List<EmcEquiptRequisitionBean> requisitionList = new ArrayList<>(validEquiptIds.size());
        validEquiptIds.forEach(equiptId -> {
            EmcEquiptRequisitionBean requisition = new EmcEquiptRequisitionBean();
            BeanUtils.copyProperties(emcEquiptRequisition, requisition);
            requisition.setEquiptId(NumberUtils.parseLong(equiptId));
            requisition.setId(ApplicationContextHelper.getNextIdentity());
            requisition.setRentUserId(LocalContextHelper.getLoginUserId());
            requisition.setIsBack(Constant.NO);
            requisitionList.add(requisition);
        });

        getDao().insert(requisitionList);

        return 0L;
    }

    @Override
    @Transactional
    @AuditTrailEntry(AuditTrailType.UPDATE)
    public void confirmBack(String ids,RestJsonWrapperBean wrapper) {
        EmcEquiptRequisitionBean newEquiptRequisition = wrapper.parseUnique(EmcEquiptRequisitionBean.class);
        if (!StringUtils.isEmpty(ids)){
            String [] str = ids.split(",");
            for (String s : str) {
                if (!StringUtils.isEmpty(s)) {
                    Long id = Long.parseLong(s);
                    EmcEquiptRequisitionBean emcEquiptRequisition = getDao().selectById(id);
                    emcEquiptRequisition.setBackUser(newEquiptRequisition.getBackUser());
                    emcEquiptRequisition.setBackUserId(LocalContextHelper.getLoginUserId());
                    emcEquiptRequisition.setBackOrg(newEquiptRequisition.getBackOrg());
                    emcEquiptRequisition.setBackDate(newEquiptRequisition.getBackDate());
                    emcEquiptRequisition.setBackTime(newEquiptRequisition.getBackTime());
                    emcEquiptRequisition.setIsBack("1");
                    emcEquiptRequisition.setBackStatus(newEquiptRequisition.getBackStatus());
                    getDao().update(emcEquiptRequisition);
                }
            }
        }
    }

    @Override
    @Transactional
    @AuditTrailEntry(AuditTrailType.UPDATE)
    public void batchChangeRentStatus(String ids, RestJsonWrapperBean wrapper) {
        EmcEquiptRequisitionBean emcEquiptRequisitionBean = wrapper.parseUnique(EmcEquiptRequisitionBean.class);
        if (!StringUtils.isEmpty(ids)){
            String [] str = ids.split(",");
            for (String s : str) {
                if (!StringUtils.isEmpty(s)) {
                    Long id = Long.parseLong(s);
                    EmcEquiptRequisitionBean emcEquiptRequisition = getDao().selectById(id);
                    emcEquiptRequisition.setRentStatus(emcEquiptRequisitionBean.getRentStatus());
                    emcEquiptRequisition.setRentDate(emcEquiptRequisitionBean.getRentDate());
                    emcEquiptRequisition.setRentTime(emcEquiptRequisitionBean.getRentTime());
                    getDao().update(emcEquiptRequisition);
                }
            }
        }
    }

    @Override
    @Transactional
    @AuditTrailEntry(AuditTrailType.UPDATE)
    public void batchChangeBackStatus(String ids, RestJsonWrapperBean wrapper) {
        EmcEquiptRequisitionBean emcEquiptRequisitionBean = wrapper.parseUnique(EmcEquiptRequisitionBean.class);
        if (!StringUtils.isEmpty(ids)){
            String [] str = ids.split(",");
            for (String s : str) {
                if (!StringUtils.isEmpty(s)) {
                    Long id = Long.parseLong(s);
                    EmcEquiptRequisitionBean emcEquiptRequisition = getDao().selectById(id);
                    emcEquiptRequisition.setBackStatus(emcEquiptRequisitionBean.getBackStatus());
                    getDao().update(emcEquiptRequisition);
                }
            }
        }
    }

    @Override
    public void delete(RestJsonWrapperBean wrapper) {
        List<EmcEquiptRequisitionBean> deleteList = wrapper.parse(this.getDao().getType());
        if (deleteList.isEmpty()){
            return;
        }
        for (EmcEquiptRequisitionBean t : deleteList) {
            getDao().delete(t.getId());
        }
    }
}
