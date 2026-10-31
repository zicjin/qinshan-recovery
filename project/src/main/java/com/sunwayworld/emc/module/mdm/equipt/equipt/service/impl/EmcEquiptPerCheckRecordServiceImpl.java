package com.sunwayworld.emc.module.mdm.equipt.equipt.service.impl;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.sunwayworld.emc.module.mdm.basemdm.equiptcategory.bean.EmcEquiptMaintBean;
import com.sunwayworld.emc.module.mdm.basemdm.equiptcategory.service.EmcEquiptMaintService;
import com.sunwayworld.emc.module.mdm.equipt.equipt.bean.EmcEquiptPerCheckPlanBean;
import com.sunwayworld.emc.module.mdm.equipt.equipt.bean.EmcEquiptPerCheckRecordBean;
import com.sunwayworld.emc.module.mdm.equipt.equipt.dao.EmcEquiptPerCheckRecordDao;
import com.sunwayworld.emc.module.mdm.equipt.equipt.service.EmcEquiptPerCheckPlanService;
import com.sunwayworld.emc.module.mdm.equipt.equipt.service.EmcEquiptPerCheckRecordService;
import com.sunwayworld.framework.at.annotation.AuditTrailEntry;
import com.sunwayworld.framework.at.annotation.AuditTrailType;
import com.sunwayworld.framework.context.ApplicationContextHelper;
import com.sunwayworld.framework.database.sql.Order;
import com.sunwayworld.framework.mybatis.mapper.MatchPattern;
import com.sunwayworld.framework.mybatis.mapper.SearchFilter;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.module.sys.code.bean.CoreCodeBean;
import com.sunwayworld.module.sys.code.service.CoreCodeService;

/**
 * @author liuyz@sunwayworld.com 2020年09月25日
 */
@Repository
public class EmcEquiptPerCheckRecordServiceImpl implements EmcEquiptPerCheckRecordService {

    @Autowired
    private EmcEquiptPerCheckRecordDao emcEquiptPerCheckRecordDao;

    @Autowired
    private EmcEquiptPerCheckPlanService emcEquiptPerCheckPlanService;

    @Autowired
    private EmcEquiptMaintService emcEquiptMaintService;

    @Autowired
    private CoreCodeService coreCodeService;

    @Override
    @SuppressWarnings("unchecked")
    public EmcEquiptPerCheckRecordDao getDao() {
        return emcEquiptPerCheckRecordDao;
    }

    @Override
    @Transactional
    @AuditTrailEntry(AuditTrailType.INSERT)
    public Long insert(RestJsonWrapperBean jsonWrapper) {
        EmcEquiptPerCheckRecordBean emcEquiptPerCheckRecordBean = jsonWrapper.parseUnique(EmcEquiptPerCheckRecordBean.class);
        emcEquiptPerCheckRecordBean.setId(ApplicationContextHelper.getNextIdentity());
        getDao().insert(emcEquiptPerCheckRecordBean);
        return emcEquiptPerCheckRecordBean.getId();
    }

    @Override
    public List<EmcEquiptMaintBean> selectRecordMaintSelectableList(Long equiptId) {
        List<EmcEquiptPerCheckPlanBean> equiptPlanList = emcEquiptPerCheckPlanService.getDao().selectListByOneColumnValue(equiptId, "equiptId");

        List<Long> idList = equiptPlanList.stream().map(p -> p.getMaintId()).collect(Collectors.toList());

        List<EmcEquiptMaintBean> emcEquiptMaintBeanList = emcEquiptMaintService.selectListByIds(idList);
        if (!emcEquiptMaintBeanList.isEmpty()){
            List<CoreCodeBean> codeBeanList = coreCodeService.selectListByFilter(SearchFilter.instance().match("codeCategoryId","maintainEvent").filter(MatchPattern.EQ), Order.asc("id"));
            for (int i = 0; i < emcEquiptMaintBeanList.size(); i++){
                for (CoreCodeBean c : codeBeanList) {
                    if (emcEquiptMaintBeanList.get(i).getMaintEvent().equals(c.getCode())){
                        emcEquiptMaintBeanList.get(i).setMaintEvent(c.getCodeName());
                    }
                }
            }
        }
        return emcEquiptMaintBeanList;
    }

    @Override
    @Transactional
    @AuditTrailEntry(AuditTrailType.UPDATE)
    public void complete(RestJsonWrapperBean wrapper) {
        List<EmcEquiptPerCheckRecordBean> emcEquipRecordList = wrapper.parse(EmcEquiptPerCheckRecordBean.class);

        List<Long> maintIdList = emcEquipRecordList.stream().map(e -> e.getMaintId()).collect(Collectors.toList());

        List<EmcEquiptPerCheckPlanBean> emcEquiptPlanList = emcEquiptPerCheckPlanService.getDao().selectListByOneColumnValues(maintIdList, "MAINTID");
        List<EmcEquiptPerCheckPlanBean> emcEquiptPlanUpdateList = new ArrayList<>();
        for (EmcEquiptPerCheckRecordBean emcEquipRecord : emcEquipRecordList) {
            emcEquipRecord.setCompleteFlag("2");

            EmcEquiptPerCheckPlanBean emcEquiptPlan = emcEquiptPlanList.stream().filter(p -> p.getEquiptId().equals(emcEquipRecord.getEquiptId())).findAny().orElse(null);

            if (emcEquiptPlan == null) {
                continue;
            }
            String[] strings = {"0-0-1", "0-0-7", "0-1-0", "0-2-0", "0-3-0", "0-4-0", "0-6-0", "1-0-0", "2-0-0", "3-0-0","5-0-0"};
            int index = Integer.parseInt(emcEquipRecord.getCycle());
            String[] cycle = strings[index-1].split("-");
            Long year = Long.parseLong(cycle[0]);
            Long month = Long.parseLong(cycle[1]);
            Long day = Long.parseLong(cycle[2]);
            LocalDateTime newMaintEndDate = emcEquipRecord.getMaintDate();
            newMaintEndDate = newMaintEndDate.plusYears(year);
            newMaintEndDate = newMaintEndDate.plusMonths(month);
            newMaintEndDate = newMaintEndDate.plusDays(day);
            emcEquiptPlan.setCycle(emcEquipRecord.getCycle());
            emcEquiptPlan.setMaintDate(emcEquipRecord.getMaintDate());
            emcEquiptPlan.setCheckReason(emcEquipRecord.getCheckReason());
            emcEquiptPlan.setCheckEquipt(emcEquipRecord.getCheckEquipt());
            emcEquiptPlan.setRemark(emcEquipRecord.getRemark());
            emcEquiptPlan.setMaintEndDate(newMaintEndDate.plusDays(-1));
            emcEquiptPlanUpdateList.add(emcEquiptPlan);
        }
        emcEquiptPerCheckPlanService.updatePlan(emcEquiptPlanUpdateList.get(0));
        getDao().update(emcEquipRecordList, "completeFlag");
    }

}
