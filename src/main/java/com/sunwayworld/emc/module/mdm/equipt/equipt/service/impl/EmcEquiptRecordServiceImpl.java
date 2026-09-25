package com.sunwayworld.emc.module.mdm.equipt.equipt.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.sunwayworld.emc.module.mdm.basemdm.equiptcategory.bean.EmcEquiptMaintBean;
import com.sunwayworld.emc.module.mdm.basemdm.equiptcategory.service.EmcEquiptMaintService;
import com.sunwayworld.emc.module.mdm.equipt.equipt.bean.EmcEquiptBean;
import com.sunwayworld.emc.module.mdm.equipt.equipt.bean.EmcEquiptPlanBean;
import com.sunwayworld.emc.module.mdm.equipt.equipt.bean.EmcEquiptRecordBean;
import com.sunwayworld.emc.module.mdm.equipt.equipt.dao.EmcEquiptRecordDao;
import com.sunwayworld.emc.module.mdm.equipt.equipt.service.EmcEquiptPlanService;
import com.sunwayworld.emc.module.mdm.equipt.equipt.service.EmcEquiptService;
import com.sunwayworld.emc.module.mdm.equipt.equipt.service.EmcEquiptRecordService;
import com.sunwayworld.framework.at.annotation.AuditTrailEntry;
import com.sunwayworld.framework.at.annotation.AuditTrailType;
import com.sunwayworld.framework.context.ApplicationContextHelper;
import com.sunwayworld.framework.data.page.Page;
import com.sunwayworld.framework.database.sql.Order;
import com.sunwayworld.framework.mybatis.mapper.MatchPattern;
import com.sunwayworld.framework.mybatis.mapper.SearchFilter;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import com.sunwayworld.module.sys.code.bean.CoreCodeBean;
import com.sunwayworld.module.sys.code.service.CoreCodeService;

/**
 * @author cuikx@sunwayworld.com
 * @date 2019-11-19
 */
@Repository
@GikamBean
public class EmcEquiptRecordServiceImpl implements EmcEquiptRecordService {

    @Autowired
    private EmcEquiptRecordDao equiptRecordDao;

    @Autowired
    private EmcEquiptPlanService equiptPlanService;

    @Autowired
    CoreCodeService coreCodeService;

    @Autowired
    @Lazy
    private EmcEquiptService equiptService;

    @Autowired
    private EmcEquiptMaintService equiptMaintService;

    @Override
    @SuppressWarnings("unchecked")
    public EmcEquiptRecordDao getDao() {
        return equiptRecordDao;
    }

    @Override
    @Transactional
    @AuditTrailEntry(AuditTrailType.INSERT)
    public Long insert(RestJsonWrapperBean jsonWrapper) {
        EmcEquiptRecordBean equiptRecordBean = jsonWrapper.parseUnique(EmcEquiptRecordBean.class);
        equiptRecordBean.setId(ApplicationContextHelper.getNextIdentity());
        getDao().insert(equiptRecordBean);
        return equiptRecordBean.getId();
    }

    @Override
    public Page<EmcEquiptRecordBean> selectRawPaginationByEquiptId(Long equiptId, RestJsonWrapperBean wrapper) {
        return selectPaginationByFilter(SearchFilter.instance().match("equiptId", equiptId).filter(MatchPattern.EQ), wrapper);
    }

    @Override
    public List<EmcEquiptMaintBean> selectRecordMaintSelectableList(Long equiptId) {
        List<EmcEquiptPlanBean> equiptPlanList = equiptPlanService.getDao().selectListByOneColumnValue(equiptId, "equiptId", new Order[0]);

        List<Long> idList = equiptPlanList.stream().map(p -> p.getMaintId()).collect(Collectors.toList());

        List<EmcEquiptMaintBean> emcEquiptMaintBeanList = equiptMaintService.selectListByIds(idList);
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
        List<EmcEquiptRecordBean> emcEquipRecordList = wrapper.parse(EmcEquiptRecordBean.class);

        List<Long> maintIdList = emcEquipRecordList.stream().map(e -> e.getMaintId()).collect(Collectors.toList());

        List<EmcEquiptPlanBean> emcEquiptPlanList = equiptPlanService.getDao().selectListByOneColumnValues(maintIdList, "MAINTID", new Order[0]);
        List<EmcEquiptPlanBean> emcEquiptPlanUpdateList = new ArrayList<>();
        List<EmcEquiptBean> emcEquiptUpdateList = new ArrayList<>();
        for (EmcEquiptRecordBean emcEquipRecord : emcEquipRecordList) {
            emcEquipRecord.setCompleteFlag("2");

            EmcEquiptPlanBean emcEquiptPlan = emcEquiptPlanList.stream().filter(p -> p.getEquiptId().equals(emcEquipRecord.getEquiptId())).findAny().orElse(null);

            if (emcEquiptPlan == null) {
                continue;
            }
            emcEquiptPlan.setCycle(emcEquipRecord.getExt$Item("cycle"));
            emcEquiptPlan.setMaintDate(emcEquipRecord.getMainDate());
            emcEquiptPlan.setMaintEndDate(emcEquipRecord.getMaintEndDate());
            emcEquiptPlan.setVerifyOrg(emcEquipRecord.getVerifyOrg());
            emcEquiptPlan.setGist(emcEquipRecord.getExt$Item("gist"));
            emcEquiptPlan.setCertCategory(emcEquipRecord.getCertCategory());
            emcEquiptPlan.setRemark(emcEquipRecord.getRemark());
            emcEquiptPlanUpdateList.add(emcEquiptPlan);
            EmcEquiptBean equiptBean = new EmcEquiptBean();
            equiptBean.setId(emcEquipRecord.getEquiptId());
            equiptBean.setSourceDate(emcEquipRecord.getMaintEndDate().toLocalDate());
            emcEquiptUpdateList.add(equiptBean);
        }
        equiptPlanService.getDao().update(emcEquiptPlanUpdateList, "CYCLE", "MAINTDATE", "MAINTENDDATE", "VERIFYORG", "GIST", "CERTCATEGORY", "REMARK");
        getDao().update(emcEquipRecordList, "completeFlag");
        equiptService.getDao().update(emcEquiptUpdateList, "SourceDate");
    }

}
