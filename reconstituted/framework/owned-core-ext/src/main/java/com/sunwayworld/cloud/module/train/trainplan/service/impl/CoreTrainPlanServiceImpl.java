/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.train.trainplan.service.impl;

import com.sunwayworld.cloud.module.train.support.enums.TrainPlanStatus;
import com.sunwayworld.cloud.module.train.trainplan.bean.CoreTrainPlanBean;
import com.sunwayworld.cloud.module.train.trainplan.persistent.dao.CoreTrainPlanDao;
import com.sunwayworld.cloud.module.train.trainplan.service.CoreTrainPlanService;
import com.sunwayworld.framework.at.annotation.AuditTrailEntry;
import com.sunwayworld.framework.at.annotation.AuditTrailType;
import com.sunwayworld.framework.audit.aunnotation.Audit;
import com.sunwayworld.framework.context.ApplicationContextHelper;
import com.sunwayworld.framework.data.page.Page;
import com.sunwayworld.framework.mybatis.mapper.MapperParameter;
import com.sunwayworld.framework.mybatis.page.PageRowBounds;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import com.sunwayworld.module.sys.bpmn.bean.CoreBpmnInstanceStatusDTO;
import com.sunwayworld.module.sys.bpmn.diagram.ProcessStatus;
import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@GikamBean
@Repository
public class CoreTrainPlanServiceImpl
implements CoreTrainPlanService {
    @Autowired
    private CoreTrainPlanDao coreTrainPlanDao;

    @Override
    public CoreTrainPlanDao getDao() {
        return this.coreTrainPlanDao;
    }

    @Override
    @Transactional
    @AuditTrailEntry(value=AuditTrailType.INSERT)
    @Audit(value="AUDIT.INSERT")
    public Long insert(RestJsonWrapperBean wrapper) {
        CoreTrainPlanBean trainPlan = wrapper.parseUnique(CoreTrainPlanBean.class);
        trainPlan.setId(ApplicationContextHelper.getNextIdentity());
        this.getDao().insert(trainPlan);
        return trainPlan.getId();
    }

    @Override
    public Page<CoreTrainPlanBean> selectRawPagination(RestJsonWrapperBean wrapper) {
        MapperParameter parameter = wrapper.extractMapFilter();
        PageRowBounds rowBounds = wrapper.extractPageRowBounds();
        return this.selectPagination(parameter, rowBounds);
    }

    @Override
    @Transactional
    @AuditTrailEntry(value=AuditTrailType.PROCESS_START)
    @Audit(value="AUDIT.PROCESS_START")
    public List<CoreBpmnInstanceStatusDTO<Long>> startProcess(RestJsonWrapperBean wrapper) {
        List<CoreBpmnInstanceStatusDTO<Long>> instanceStatusList = CoreTrainPlanService.super.startProcess(wrapper);
        ArrayList itemList4Update = new ArrayList();
        instanceStatusList.forEach(e -> {
            CoreTrainPlanBean trainPlan = new CoreTrainPlanBean();
            trainPlan.setStatus(TrainPlanStatus.APPROVE.name().toLowerCase());
            trainPlan.setId((Long)e.getId());
            itemList4Update.add(trainPlan);
        });
        this.getDao().update(itemList4Update, new String[]{"STATUS"});
        return instanceStatusList;
    }

    @Override
    @Transactional
    @AuditTrailEntry(value=AuditTrailType.PROCESS_REJECT)
    @Audit(value="AUDIT.PROCESS_REJECT")
    public List<CoreBpmnInstanceStatusDTO<Long>> rejectTask(RestJsonWrapperBean wrapper) {
        List<CoreBpmnInstanceStatusDTO<Long>> instanceStatusList = CoreTrainPlanService.super.rejectTask(wrapper);
        ArrayList itemList4Update = new ArrayList();
        instanceStatusList.forEach(e -> {
            CoreTrainPlanBean trainPlan = new CoreTrainPlanBean();
            trainPlan.setStatus(TrainPlanStatus.DRAFT_REJECTED.name().toLowerCase());
            trainPlan.setId((Long)e.getId());
            itemList4Update.add(trainPlan);
        });
        this.getDao().update(itemList4Update, new String[]{"STATUS"});
        return instanceStatusList;
    }

    @Override
    @Transactional
    @AuditTrailEntry(value=AuditTrailType.PROCESS_COMPLETE)
    @Audit(value="AUDIT.PROCESS_COMPLETE")
    public List<CoreBpmnInstanceStatusDTO<Long>> completeTask(RestJsonWrapperBean wrapper) {
        List<CoreBpmnInstanceStatusDTO<Long>> instanceStatusList = CoreTrainPlanService.super.completeTask(wrapper);
        ArrayList updateItemList = new ArrayList();
        instanceStatusList.stream().filter(f -> ProcessStatus.DONE.equals((Object)f.getProcessStatus())).forEach(e -> {
            CoreTrainPlanBean item = new CoreTrainPlanBean();
            item.setId((Long)e.getId());
            item.setStatus(TrainPlanStatus.TO_IMPLEMENT.name().toLowerCase());
            updateItemList.add(item);
        });
        if (!updateItemList.isEmpty()) {
            this.getDao().update(updateItemList, new String[]{"STATUS"});
        }
        return instanceStatusList;
    }
}
