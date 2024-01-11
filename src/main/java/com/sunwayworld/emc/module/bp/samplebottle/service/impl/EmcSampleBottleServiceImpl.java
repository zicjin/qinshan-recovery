package com.sunwayworld.emc.module.bp.samplebottle.service.impl;

import com.sunwayworld.framework.context.LocalContextHelper;
import com.sunwayworld.framework.log.annotation.Log;
import com.sunwayworld.framework.log.annotation.LogType;
import com.sunwayworld.framework.mybatis.mapper.MatchPattern;
import com.sunwayworld.framework.mybatis.mapper.SearchFilter;
import com.sunwayworld.module.mdm.org.bean.CoreOrgBean;
import com.sunwayworld.module.mdm.org.service.CoreOrgService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.sunwayworld.framework.at.annotation.AuditTrailType;
import com.sunwayworld.framework.at.annotation.AuditTrailEntry;
import com.sunwayworld.framework.spring.annotation.GikamBean;

import com.sunwayworld.emc.module.bp.samplebottle.dao.EmcSampleBottleDao;
import com.sunwayworld.emc.module.bp.samplebottle.bean.EmcSampleBottleBean;
import com.sunwayworld.emc.module.bp.samplebottle.service.EmcSampleBottleService;
import com.sunwayworld.framework.context.ApplicationContextHelper;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;

import java.util.List;

@Repository
@GikamBean
public class EmcSampleBottleServiceImpl implements EmcSampleBottleService {

    @Autowired
    private EmcSampleBottleDao emcSampleBottleDao;

    @Override
    @SuppressWarnings("unchecked")
    public EmcSampleBottleDao getDao() {
        return emcSampleBottleDao;
    }

    @Autowired
    private CoreOrgService orgService;

    @Override
    @Transactional
    @AuditTrailEntry(AuditTrailType.INSERT)
    public Long insert(RestJsonWrapperBean jsonWrapper) {
        EmcSampleBottleBean emcSampleBottle = jsonWrapper.parseUnique(EmcSampleBottleBean.class);
        String projNo= emcSampleBottle.getProjNo();
        String  parentId = LocalContextHelper.getLoginUser().getExt$Item("parentorgid");
        List<CoreOrgBean> sampleOrgList = orgService.selectListByFilter(SearchFilter.instance().match("id", parentId).filter(MatchPattern.EQ));
        CoreOrgBean coreOrg= sampleOrgList.get(0);
        String orgShortName = "T"+coreOrg.getExt$Item("orgshortname");
        String sampleBottleNumber = ApplicationContextHelper.getNextSequence(getDao().getTable(), orgShortName);
        emcSampleBottle.setProjNo(projNo);
        emcSampleBottle.setSampleBottleNo(sampleBottleNumber);
        emcSampleBottle.setId(ApplicationContextHelper.getNextIdentity());
        emcSampleBottle.setOrgId(coreOrg.getId());
        getDao().insert(emcSampleBottle);
        return emcSampleBottle.getId();
    }

    @Override
    @Log(value = "根据VOC瓶数量插入VOC瓶", type = LogType.INSERT)
    @Transactional
    @AuditTrailEntry(AuditTrailType.INSERT)
    public String addSampleBottle(RestJsonWrapperBean jsonWrapper) {
        //插入数据次数
        String addnum= jsonWrapper.getFilterValue("addnum");
        if (addnum != null && addnum.length() != 0){
            int addnum1 = Integer.parseInt(addnum);
            EmcSampleBottleBean emcSampleBottle = new EmcSampleBottleBean();
            String  parentId =LocalContextHelper.getLoginUser().getExt$Item("parentorgid");
            List<CoreOrgBean> sampleOrgList = orgService.selectListByFilter(SearchFilter.instance().match("id", parentId).filter(MatchPattern.EQ));
            CoreOrgBean coreOrg= sampleOrgList.get(0);
            String orgShortName = "T"+coreOrg.getExt$Item("orgshortname");
            for (int i =1;i<=addnum1;i++) {
                String sampleBottleNumber = ApplicationContextHelper.getNextSequence(getDao().getTable(), orgShortName);
                emcSampleBottle.setId(ApplicationContextHelper.getNextIdentity());
                emcSampleBottle.setSampleBottleNo(sampleBottleNumber);
                emcSampleBottle.setOrgId(coreOrg.getId());
                getDao().insert(emcSampleBottle);
            }
        }
        return addnum;

    }
}
