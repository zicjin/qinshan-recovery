package com.sunwayworld.emc.module.mdm.equipt.equiptcalibrationresult.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.sunwayworld.emc.module.mdm.basemdm.equiptcategory.bean.EmcEquiptMaintBean;
import com.sunwayworld.emc.module.mdm.basemdm.equiptcategory.service.EmcEquiptMaintService;
import com.sunwayworld.emc.module.mdm.equipt.equipt.bean.EmcEquiptBean;
import com.sunwayworld.emc.module.mdm.equipt.equipt.bean.EmcEquiptPlanBean;
import com.sunwayworld.emc.module.mdm.equipt.equipt.bean.EmcEquiptRecordBean;
import com.sunwayworld.emc.module.mdm.equipt.equipt.service.EmcEquiptPlanService;
import com.sunwayworld.emc.module.mdm.equipt.equipt.service.EmcEquiptRecordService;
import com.sunwayworld.emc.module.mdm.equipt.equipt.service.EmcEquiptService;
import com.sunwayworld.emc.module.mdm.equipt.equiptcalibrationresult.bean.EmcEquiptCaliResultBean;
import com.sunwayworld.emc.module.mdm.equipt.equiptcalibrationresult.dao.EmcEquiptCaliResultDao;
import com.sunwayworld.emc.module.mdm.equipt.equiptcalibrationresult.service.EmcEquiptCaliResultService;
import com.sunwayworld.emc.module.mdm.equipt.equiptcaliresultnorm.bean.EmcEquiptCaliResultNormBean;
import com.sunwayworld.emc.module.mdm.equipt.equiptcaliresultnorm.service.EmcEquiptCaliResultNormService;
import com.sunwayworld.framework.at.annotation.AuditTrailEntry;
import com.sunwayworld.framework.at.annotation.AuditTrailType;
import com.sunwayworld.framework.context.ApplicationContextHelper;
import com.sunwayworld.framework.data.page.Page;
import com.sunwayworld.framework.mybatis.mapper.MapperParameter;
import com.sunwayworld.framework.mybatis.mapper.MatchPattern;
import com.sunwayworld.framework.mybatis.mapper.SearchFilter;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;

@Repository
public class EmcEquiptCaliResultServiceImpl implements EmcEquiptCaliResultService {

    @Autowired
    private EmcEquiptCaliResultDao emcEquiptCaliResultDao;

    @Autowired
    private EmcEquiptService emcEquiptService;

    @Autowired
    private EmcEquiptPlanService emcEquiptPlanService;

    @Autowired
    private EmcEquiptRecordService emcEquiptRecordService;

    @Autowired
    private EmcEquiptCaliResultNormService emcEquiptCaliResultNormService;

    @Autowired
    private EmcEquiptMaintService emcEquiptMaintService;

    @Override
    @SuppressWarnings("unchecked")
    public EmcEquiptCaliResultDao getDao() {
        return emcEquiptCaliResultDao;
    }

    @Override
    @Transactional
    @AuditTrailEntry(AuditTrailType.INSERT)
    public Long insert(RestJsonWrapperBean jsonWrapper) {
        List<EmcEquiptCaliResultBean> emcEquiptCaliResultList = jsonWrapper.parse(EmcEquiptCaliResultBean.class);
        List<Long> generateIdList = ApplicationContextHelper.getNextIdentityList(emcEquiptCaliResultList.size());
        for (int i = 0; i < emcEquiptCaliResultList.size(); i++) {
            emcEquiptCaliResultList.get(i).setId(generateIdList.get(i));
        }
        getDao().insert(emcEquiptCaliResultList);
        return emcEquiptCaliResultList.get(0).getId();
    }

    @Override
    public Long insertTestMethod(Long id, RestJsonWrapperBean wrapper) {
        return emcEquiptCaliResultNormService.insert(id, wrapper);
    }

    @Override
    public Page<EmcEquiptCaliResultNormBean> selectEquiptTestMethodRawPagination(Long id, RestJsonWrapperBean wrapper) {
        return emcEquiptCaliResultNormService.selectPaginationByFilter(SearchFilter.instance().match("CALIRESULTID", id).filter(MatchPattern.EQ), wrapper);
    }

    @Override
    @Transactional
    public void updateEquiptPlanOnPass(RestJsonWrapperBean wrapper) {
        List<EmcEquiptCaliResultBean> caliResultList = wrapper.parse(EmcEquiptCaliResultBean.class);
        if (!caliResultList.isEmpty()) {
            caliResultList.forEach(item -> {
                //查询仪器信息
                List<EmcEquiptBean> equiptBeanList = emcEquiptService.getDao().selectListByOneColumnValue(item.getEquiptId(), "equiptId");
                if (equiptBeanList.size() != 1){
                    return;
                }
                //查询仪器维护事件
                MapperParameter mapperParameter = new MapperParameter();
                mapperParameter.setFilter(SearchFilter.instance().match("equiptCategoryId",equiptBeanList.get(0).getEquiptCategoryId()).filter(MatchPattern.EQ)
                .match("maintEvent","equiptCalibration").filter(MatchPattern.EQ));
                EmcEquiptMaintBean equiptMaintBean = emcEquiptMaintService.selectFirst(mapperParameter);
                //查询是否已有维护计划
                EmcEquiptPlanBean planBean = emcEquiptPlanService.selectFirstByFilter(SearchFilter.instance().match("equiptId", equiptBeanList.get(0).getId()).filter(MatchPattern.EQ)
                                              .match("maintId",equiptMaintBean.getId()).filter(MatchPattern.EQ));
                if (planBean == null){
                    planBean = new EmcEquiptPlanBean();
                    planBean.setId(ApplicationContextHelper.getNextIdentity());
                    planBean.setEquiptId(equiptBeanList.get(0).getId());
                    planBean.setMaintId(equiptMaintBean.getId());
                    planBean.setWarningDays(equiptMaintBean.getWarningDays());
                    emcEquiptPlanService.getDao().insert(planBean);
                }
                planBean.setCycle(item.getCycle());
                planBean.setMaintEndDate(item.getValidTime());
                planBean.setGist(item.getCaliReason());
                planBean.setMaintDate(item.getMaintDate());
                planBean.setVerifyOrg(item.getUnit());
                planBean.setGist(item.getCaliReason());
                planBean.setCertCategory(item.getReportCategory());
                planBean.setRemark(item.getRemark());
                //emcEquiptPlanService.updatePlan(planBean);
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
                recordBean.setCertCategory(item.getReportCategory());
                recordBean.setCertNo(item.getCertNo());
                recordBean.setRemark(item.getRemark());
                emcEquiptRecordService.getDao().insert(recordBean);

                EmcEquiptBean emcEquipt = equiptBeanList.get(0);
                emcEquipt.setSourceMethod("calibration");
                emcEquipt.setSourceDate(recordBean.getMaintEndDate().toLocalDate());
                emcEquiptService.update(emcEquipt);
            });
        }
    }
}
