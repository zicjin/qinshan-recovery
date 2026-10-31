package com.sunwayworld.emc.module.mdm.equipt.equiptpercheck.service.impl;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.sunwayworld.emc.framework.constant.EmcBizConstant;
import com.sunwayworld.emc.module.mdm.basemdm.equiptcategory.bean.EmcEquiptMaintBean;
import com.sunwayworld.emc.module.mdm.basemdm.equiptcategory.service.EmcEquiptMaintService;
import com.sunwayworld.emc.module.mdm.equipt.equipt.bean.EmcEquiptBean;
import com.sunwayworld.emc.module.mdm.equipt.equipt.bean.EmcEquiptPerCheckPlanBean;
import com.sunwayworld.emc.module.mdm.equipt.equipt.bean.EmcEquiptPerCheckRecordBean;
import com.sunwayworld.emc.module.mdm.equipt.equipt.service.EmcEquiptPerCheckPlanService;
import com.sunwayworld.emc.module.mdm.equipt.equipt.service.EmcEquiptPerCheckRecordService;
import com.sunwayworld.emc.module.mdm.equipt.equipt.service.EmcEquiptService;
import com.sunwayworld.emc.module.mdm.equipt.equiptpercheck.bean.EmcEquiptPerCheckBean;
import com.sunwayworld.emc.module.mdm.equipt.equiptpercheck.dao.EmcEquiptPerCheckDao;
import com.sunwayworld.emc.module.mdm.equipt.equiptpercheck.service.EmcEquiptPerCheckService;
import com.sunwayworld.framework.at.annotation.AuditTrailEntry;
import com.sunwayworld.framework.at.annotation.AuditTrailType;
import com.sunwayworld.framework.context.ApplicationContextHelper;
import com.sunwayworld.framework.context.LocalContextHelper;
import com.sunwayworld.framework.mybatis.mapper.MapperParameter;
import com.sunwayworld.framework.mybatis.mapper.MatchPattern;
import com.sunwayworld.framework.mybatis.mapper.SearchFilter;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import com.sunwayworld.framework.utils.NumberUtils;
import com.sunwayworld.module.sys.bpmn.bean.CoreBpmnInstanceStatusDTO;

@Repository
@GikamBean
public class EmcEquiptPerCheckServiceImpl implements EmcEquiptPerCheckService {

    public static String START_FLAG = "START";

    public static String COMPELTE_FLAG = "COMPELTE";

    public static String STATUS_CODE = "preLogged";

    @Autowired
    private EmcEquiptPerCheckDao emcEquiptPerCheckDao;

    @Autowired
    private EmcEquiptService emcEquiptService;

    @Autowired
    private EmcEquiptMaintService emcEquiptMaintService;

    @Autowired
    private EmcEquiptPerCheckPlanService emcEquiptPerCheckPlanService;

    @Autowired
    private EmcEquiptPerCheckRecordService emcEquiptPerCheckRecordService;

    @Override
    @SuppressWarnings("unchecked")
    public EmcEquiptPerCheckDao getDao() {
        return emcEquiptPerCheckDao;
    }

    @Override
    @Transactional
    @AuditTrailEntry(AuditTrailType.INSERT)
    public Long insert(RestJsonWrapperBean jsonWrapper) {
        List<EmcEquiptPerCheckBean> emcEquiptPerCheckList = jsonWrapper.parse(EmcEquiptPerCheckBean.class);
        List<Long> generateIdList = ApplicationContextHelper.getNextIdentityList(emcEquiptPerCheckList.size());
        for (int i = 0; i < emcEquiptPerCheckList.size(); i++){
            emcEquiptPerCheckList.get(i).setId(generateIdList.get(i));
            emcEquiptPerCheckList.get(i).setCheckPerson(LocalContextHelper.getLoginUserName());
        }
        getDao().insert(emcEquiptPerCheckList);
        return emcEquiptPerCheckList.get(0).getId();
    }

    @Override
    @Transactional
    @AuditTrailEntry(AuditTrailType.UPDATE)
    public void updateEquiptPlanOnPass(RestJsonWrapperBean wrapper) {
        List<EmcEquiptPerCheckBean> perCheckResultList = wrapper.parse(EmcEquiptPerCheckBean.class);
        if (!perCheckResultList.isEmpty()) {
            perCheckResultList.forEach(item -> {
                //查询仪器信息
                List<EmcEquiptBean> equiptBeanList = emcEquiptService.getDao().selectListByOneColumnValue(item.getEquiptId(), "equiptId");
                if (equiptBeanList.size() != 1){
                    return;
                }
                //查询仪器维护事件
                MapperParameter mapperParameter = new MapperParameter();
                mapperParameter.setFilter(SearchFilter.instance().match("equiptCategoryId",equiptBeanList.get(0).getEquiptCategoryId()).filter(MatchPattern.EQ)
                        .match("maintEvent","duringCheck").filter(MatchPattern.EQ));
                EmcEquiptMaintBean equiptMaintBean = emcEquiptMaintService.selectFirst(mapperParameter);
                //查询是否已有维护计划
                EmcEquiptPerCheckPlanBean planBean = emcEquiptPerCheckPlanService.selectFirstByFilter(SearchFilter.instance().match("equiptId", equiptBeanList.get(0).getId()).filter(MatchPattern.EQ)
                        .match("maintId",equiptMaintBean.getId()).filter(MatchPattern.EQ));
                if (planBean == null){
                    planBean = new EmcEquiptPerCheckPlanBean();
                    planBean.setId(ApplicationContextHelper.getNextIdentity());
                    planBean.setEquiptId(equiptBeanList.get(0).getId());
                    planBean.setMaintId(equiptMaintBean.getId());
                    planBean.setWarningDays(equiptMaintBean.getWarningDays());
                    planBean.setMaintEvent(equiptMaintBean.getMaintEvent());
                    emcEquiptPerCheckPlanService.getDao().insert(planBean);
                }

                if (NumberUtils.isNumber(item.getCycle())) {
                    String[] strings = {"0-0-1", "0-0-7", "0-1-0", "0-2-0", "0-3-0", "0-4-0", "0-6-0", "1-0-0", "2-0-0", "3-0-0","5-0-0"};
                    int index = Integer.parseInt(item.getCycle());
                    String[] cycle = strings[index-1].split("-");
                    long year = Long.parseLong(cycle[0]);
                    long month = Long.parseLong(cycle[1]);
                    long day = Long.parseLong(cycle[2]);
                    LocalDateTime newMaintEndDate = item.getCheckTime();
                    newMaintEndDate = newMaintEndDate.plusYears(year);
                    newMaintEndDate = newMaintEndDate.plusMonths(month);
                    newMaintEndDate = newMaintEndDate.plusDays(day);
                    planBean.setMaintEndDate(newMaintEndDate);
                }

                planBean.setCycle(item.getCycle());
                planBean.setMaintDate(item.getCheckTime());
                planBean.setCheckReason(item.getCheckReason());
                planBean.setCheckEquipt(item.getEquiptment());
                planBean.setRemark(item.getRemark());
                emcEquiptPerCheckPlanService.updatePlan(planBean);

                EmcEquiptPerCheckRecordBean recordBean = new EmcEquiptPerCheckRecordBean();
                recordBean.setCompleteFlag("2");
                recordBean.setId(ApplicationContextHelper.getNextIdentity());
                recordBean.setEquiptId(planBean.getEquiptId());
                recordBean.setMaintDate(planBean.getMaintDate());
                recordBean.setCycle(planBean.getCycle());
                recordBean.setCheckReason(planBean.getCheckReason());
                recordBean.setCheckEquipt(planBean.getCheckEquipt());
                recordBean.setCheckUsernames(item.getCheckPerson());

                recordBean.setCreatedByName(item.getCreatedByName());
                recordBean.setCreatedTime(item.getCreatedTime());
                recordBean.setMaintId(planBean.getMaintId());
                recordBean.setRemark(item.getRemark());
                emcEquiptPerCheckRecordService.getDao().insert(recordBean);
            });
        }
    }

    @Override
    @Transactional
    @AuditTrailEntry(AuditTrailType.PROCESS_START)
    public List<CoreBpmnInstanceStatusDTO<Long>> startProcess(RestJsonWrapperBean wrapper) {

        // 核查记录提交
        List<CoreBpmnInstanceStatusDTO<Long>> coreBpmnInstanceStatusDTOList = getProcessBasic(wrapper,START_FLAG);
        List<Long> idList = coreBpmnInstanceStatusDTOList.stream().map(e -> e.getId()).collect(Collectors.toList());

        //更新提交时间为记录时间,更新状态
        List<EmcEquiptPerCheckBean> equiptPerCheckList = getDao().selectListByIds(idList);
        LocalDateTime recorderDate = LocalDateTime.now();
        equiptPerCheckList.stream().forEach(a -> {
            a.setRecorderDate(recorderDate);
            a.setProcessStatus(EmcBizConstant.PROJECT_PROCESSSTATUS_APPROVE);
        });
        getDao().update(equiptPerCheckList, "RECORDERDATE","PROCESSSTATUS");

        return coreBpmnInstanceStatusDTOList;
    }


    @Override
    @Transactional
    @AuditTrailEntry(AuditTrailType.PROCESS_COMPLETE)
    public List<CoreBpmnInstanceStatusDTO<Long>> completeTask(RestJsonWrapperBean wrapper) {
        // 核查记录主管审核
        List<CoreBpmnInstanceStatusDTO<Long>> coreBpmnInstanceStatusDTOList = getProcessBasic(wrapper,COMPELTE_FLAG);
        List<Long> idList = coreBpmnInstanceStatusDTOList.stream().map(e -> e.getId()).collect(Collectors.toList());

        String status = coreBpmnInstanceStatusDTOList.get(0).getStatusCode();
        //更新提交时间为主管记录时间,更新状态
        List<EmcEquiptPerCheckBean> equiptPerCheckList = getDao().selectListByIds(idList);
        LocalDateTime nowDateTiem = LocalDateTime.now();
        if (STATUS_CODE.equals(status)){
            //更新提交时间为主管记录时间,更新状态
            equiptPerCheckList.stream().forEach(a -> {
                a.setDirectorDate(nowDateTiem);
                a.setProcessStatus(EmcBizConstant.PROJECT_PROCESSSTATUS_APPROVE);
            });
            getDao().update(equiptPerCheckList, "DIRECTORDATE","PROCESSSTATUS");
        }else {
            //更新提交时间为技术负责人记录时间,更新状态
            equiptPerCheckList.stream().forEach(a -> {
                a.setPrincipalDate(nowDateTiem);
                a.setProcessStatus(EmcBizConstant.PROJECT_PROCESSSTATUS_DONE);
            });
            getDao().update(equiptPerCheckList, "PRINCIPALDATE","PROCESSSTATUS");
        }


        return coreBpmnInstanceStatusDTOList;
    }

    // 提取工作流方法
    public List<CoreBpmnInstanceStatusDTO<Long>> getProcessBasic(RestJsonWrapperBean wrapper,String flag) {

        List<CoreBpmnInstanceStatusDTO<Long>> coreBpmnInstanceStatusDTOList = new ArrayList<>();
        if (START_FLAG.equals(flag)) {
           coreBpmnInstanceStatusDTOList = EmcEquiptPerCheckService.super.startProcess(wrapper);
        }else if (COMPELTE_FLAG.equals(flag)){
            coreBpmnInstanceStatusDTOList = EmcEquiptPerCheckService.super.completeTask(wrapper);
        }
        return coreBpmnInstanceStatusDTOList.isEmpty()?null:coreBpmnInstanceStatusDTOList;
    }
}
