package com.sunwayworld.emc.module.mdm.equipt.verification.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.sunwayworld.emc.module.mdm.equipt.verification.bean.EmcEquipVerificationBean;
import com.sunwayworld.emc.module.mdm.equipt.verification.dao.EmcEquipVerificationDao;
import com.sunwayworld.emc.module.mdm.equipt.verification.service.EmcEquipVerificationService;
import com.sunwayworld.emc.module.mdm.basemdm.equiptcategory.bean.EmcEquiptMaintBean;
import com.sunwayworld.emc.module.mdm.basemdm.equiptcategory.service.EmcEquiptMaintService;
import com.sunwayworld.emc.module.mdm.equipt.equipt.bean.EmcEquiptBean;
import com.sunwayworld.emc.module.mdm.equipt.equipt.bean.EmcEquiptPlanBean;
import com.sunwayworld.emc.module.mdm.equipt.equipt.bean.EmcEquiptRecordBean;
import com.sunwayworld.emc.module.mdm.equipt.equipt.service.EmcEquiptPlanService;
import com.sunwayworld.emc.module.mdm.equipt.equipt.service.EmcEquiptRecordService;
import com.sunwayworld.emc.module.mdm.equipt.equipt.service.EmcEquiptService;
import com.sunwayworld.framework.at.annotation.AuditTrailEntry;
import com.sunwayworld.framework.at.annotation.AuditTrailType;
import com.sunwayworld.framework.context.ApplicationContextHelper;
import com.sunwayworld.framework.mybatis.mapper.MapperParameter;
import com.sunwayworld.framework.mybatis.mapper.MatchPattern;
import com.sunwayworld.framework.mybatis.mapper.SearchFilter;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.spring.annotation.GikamBean;

@Repository
@GikamBean
public class EmcEquipVerificationServiceImpl implements EmcEquipVerificationService {

    @Autowired
    private EmcEquipVerificationDao emcEquipVerificationDao;

    @Autowired
    private EmcEquiptService emcEquiptService;

    @Autowired
    private EmcEquiptMaintService emcEquiptMaintService;

    @Autowired
    private EmcEquiptPlanService emcEquiptPlanService;

    @Autowired
    private EmcEquiptRecordService emcEquiptRecordService;

    @Override
    @SuppressWarnings("unchecked")
    public EmcEquipVerificationDao getDao() {
        return emcEquipVerificationDao;
    }

    @Override
    @Transactional
    @AuditTrailEntry(AuditTrailType.INSERT)
    public Long insert(RestJsonWrapperBean wrapper) {
        List<EmcEquipVerificationBean> verificationList = wrapper.parse(EmcEquipVerificationBean.class);
        verificationList.forEach(e -> {
            e.setId(ApplicationContextHelper.getNextIdentity());
        });
        getDao().insert(verificationList);
        return verificationList.isEmpty() ? null : verificationList.get(0).getId();
    }

    @Override
    @Transactional
    public void updateEquiptPlanOnPass(RestJsonWrapperBean wrapper) {
        List<EmcEquipVerificationBean> verificationList = wrapper.parse(EmcEquipVerificationBean.class);
        if (!verificationList.isEmpty()) {
            verificationList.forEach(item -> {
                EmcEquiptBean equiptBean = emcEquiptService.getDao().selectById(item.getEquiptId());
                if (equiptBean == null){
                    return;
                }
                MapperParameter mapperParameter = new MapperParameter();
                mapperParameter.setFilter(SearchFilter.instance().match("equiptCategoryId",equiptBean.getEquiptCategoryId()).filter(MatchPattern.EQ)
                        .match("maintEvent","equiptVerification").filter(MatchPattern.EQ));
                EmcEquiptMaintBean equiptMaintBean = emcEquiptMaintService.selectFirst(mapperParameter);

                EmcEquiptPlanBean planBean = emcEquiptPlanService.selectFirstByFilter(SearchFilter.instance().match("equiptId", equiptBean.getId()).filter(MatchPattern.EQ)
                        .match("maintId",equiptMaintBean.getId()).filter(MatchPattern.EQ));
                if (planBean == null) {
                    planBean = new EmcEquiptPlanBean();
                    planBean.setId(ApplicationContextHelper.getNextIdentity());
                    planBean.setEquiptId(equiptBean.getId());
                    planBean.setMaintId(equiptMaintBean.getId());
                    planBean.setWarningDays(equiptMaintBean.getWarningDays());
                    emcEquiptPlanService.getDao().insert(planBean);
                }
                planBean.setCycle(item.getExt$Item("cycle"));
                planBean.setMaintEndDate(item.getValidity());
                planBean.setGist(item.getExt$Item("verificationreason"));
                planBean.setMaintDate(item.getMaintDate());
                planBean.setVerifyOrg(item.getUnit());
                planBean.setCertCategory(item.getCertCategory());
                planBean.setRemark(item.getRemark());
                /*emcEquiptPlanService.updatePlan(planBean);*/
                emcEquiptPlanService.getDao().update(planBean, "CYCLE", "MAINTDATE", "MAINTENDDATE", "VERIFYORG", "GIST", "CERTCATEGORY", "REMARK");

                EmcEquiptRecordBean recordBean = new EmcEquiptRecordBean();
                recordBean.setCompleteFlag("2");
                recordBean.setId(ApplicationContextHelper.getNextIdentity());
                recordBean.setEquiptId(planBean.getEquiptId());
                recordBean.setMainDate(planBean.getMaintDate());
                recordBean.setMaintEndDate(planBean.getMaintEndDate());
                recordBean.setVerifyOrg(planBean.getVerifyOrg());
                recordBean.setExt$Item("cycle",planBean.getCycle());
                recordBean.setCreatedByName(item.getCreatedByName());
                recordBean.setCreatedTime(item.getCreatedTime());
                recordBean.setMaintId(planBean.getMaintId());
                recordBean.setExt$Item("gist",planBean.getGist());
                recordBean.setCertNo(item.getCertNo());
                recordBean.setCertCategory(item.getCertCategory());
                recordBean.setRemark(item.getRemark());
                emcEquiptRecordService.getDao().insert(recordBean);

                equiptBean.setSourceDate(recordBean.getMaintEndDate().toLocalDate());
                equiptBean.setSourceMethod("verification");
                emcEquiptService.update(equiptBean);
            });
        }
    }
}
