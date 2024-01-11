package com.sunwayworld.emc.module.mdm.bizmdm.test.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.sunwayworld.framework.utils.ObjectUtils;
import com.sunwayworld.framework.utils.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.sunwayworld.emc.module.mdm.bizmdm.test.bean.EmcTestMethodBean;
import com.sunwayworld.emc.module.mdm.bizmdm.test.dao.EmcTestMethodEquiptDao;
import com.sunwayworld.emc.module.mdm.bizmdm.test.service.EmcTestMethodService;
import com.sunwayworld.emc.module.mdm.equipt.equipt.bean.EmcEquiptBean;
import com.sunwayworld.emc.module.mdm.equipt.equipt.bean.EmcEquiptTestMethodBean;
import com.sunwayworld.emc.module.mdm.equipt.equipt.service.EmcEquiptService;
import com.sunwayworld.emc.module.mdm.equipt.equipt.service.EmcEquiptTestMethodService;
import com.sunwayworld.framework.at.annotation.AuditTrailEntry;
import com.sunwayworld.framework.at.annotation.AuditTrailType;
import com.sunwayworld.framework.context.ApplicationContextHelper;
import com.sunwayworld.framework.context.LocalContextHelper;
import com.sunwayworld.framework.data.page.Page;
import com.sunwayworld.framework.mybatis.mapper.MatchPattern;
import com.sunwayworld.framework.mybatis.mapper.SearchFilter;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import com.sunwayworld.module.mdm.org.bean.CoreOrgBean;
import com.sunwayworld.module.mdm.org.service.CoreOrgService;
import com.sunwayworld.module.mdm.user.bean.CoreUserBean;

@GikamBean
@Repository
public class PfTestMethodEquiptServiceImpl extends EmcTestMethodEquiptServiceImpl{

    @Autowired
    private EmcTestMethodEquiptDao emcTestMethodEquiptDao;

    @Autowired
    private EmcEquiptTestMethodService equiptTestMethodService;

    @Autowired
    @Lazy
    private EmcTestMethodService testMethodService;

    @Autowired
    @Lazy
    private EmcEquiptService equiptService;

    @Autowired
    private CoreOrgService orgService;

    @Override
    @SuppressWarnings("unchecked")
    public EmcTestMethodEquiptDao getDao() {
        return emcTestMethodEquiptDao;
    }

    @Override
    @Transactional
    @AuditTrailEntry(AuditTrailType.INSERT)
    public void insert(Long testMethodId, RestJsonWrapperBean jsonWrapper) {
        List<EmcEquiptTestMethodBean> testEquiptList = jsonWrapper.parse(EmcEquiptTestMethodBean.class);

        EmcTestMethodBean testMethod = testMethodService.getDao().selectById(testMethodId);
        testEquiptList.stream().forEach(e -> {
            e.setId(ApplicationContextHelper.getNextIdentity());
            e.setTestMethodId(testMethodId);
            e.setTestCategoryId(testMethod.getTestCategoryId());
            e.setTestId(testMethod.getTestId());
            e.setMethodId(testMethod.getMethodId());
            /*Long equiptId = e.getEquiptId();
            EmcEquiptBean emcEquiptBean = equiptService.getDao().selectById(equiptId);
            String departId = emcEquiptBean.getUsedDepartmentId();
            CoreOrgBean coreOrg = orgService.getDao().selectById(departId);
            String orgName = coreOrg.getOrgName();
            if ("现场室".equals(orgName)) {
                e.setAnalysisCategory("sampling");
            } else {
                e.setAnalysisCategory("analysis");
            }*/
        });

        equiptTestMethodService.getDao().insert(testEquiptList);
    }
    
    @Override
    public Page<EmcEquiptTestMethodBean> selectEquiptPagationByTestMethod(Long id, RestJsonWrapperBean wrapper) {
        CoreUserBean user = LocalContextHelper.getLoginUser();
        //赋值所属单位和流程类型
        String parentOrgId = user.getExt$Item("parentorgid");
        return equiptTestMethodService.selectPaginationByFilter(SearchFilter.instance().match("ORGID", parentOrgId).filter(MatchPattern.EQ).match("TESTMETHODID", id).filter(MatchPattern.EQ), wrapper);
    }

    @Override
    @Transactional
    @AuditTrailEntry(AuditTrailType.INSERT)
    public void copyTo(RestJsonWrapperBean jsonWrapper) {
        List<EmcEquiptTestMethodBean> copyEquiptList = jsonWrapper.parse(EmcEquiptTestMethodBean.class);
        List<EmcTestMethodBean> targetTestMethodList = jsonWrapper.parse(EmcTestMethodBean.class);
        String parentOrgId = LocalContextHelper.getLoginUser().getExt$Item("parentOrgId");

        List<Long> targetTestMethodIdList = targetTestMethodList.stream().map(EmcTestMethodBean::getId).collect(Collectors.toList());
        List<EmcEquiptTestMethodBean> existedEquiptList = equiptTestMethodService.selectListByFilter(SearchFilter.instance().match("testMethodId", targetTestMethodIdList).filter(MatchPattern.OR).match("orgId", parentOrgId).filter(MatchPattern.SEQ));

        List<EmcEquiptTestMethodBean> newEquiptList = new ArrayList<>();

        targetTestMethodList.forEach(testMethod -> {
            List<Long> curExistedEquiptIdList = existedEquiptList.stream().filter(e -> ObjectUtils.equals(e.getTestMethodId(), testMethod.getId()) && StringUtils.equals(e.getExt$Item("orgId"), parentOrgId)).map(EmcEquiptTestMethodBean::getEquiptId).distinct().collect(Collectors.toList());
            copyEquiptList.stream().filter(ce -> !curExistedEquiptIdList.contains(ce.getEquiptId())).forEach(ce -> {
                EmcEquiptTestMethodBean newEquipt = new EmcEquiptTestMethodBean();
                newEquipt.setId(ApplicationContextHelper.getNextIdentity());
                newEquipt.setEquiptId(ce.getEquiptId());
                newEquipt.setTestMethodId(testMethod.getId());
                newEquipt.setTestId(testMethod.getTestId());
                newEquipt.setTestCategoryId(testMethod.getTestCategoryId());
                newEquipt.setMethodId(testMethod.getMethodId());
                newEquipt.setOrgId(ce.getOrgId());
                newEquiptList.add(newEquipt);
            });
        });

        if (!newEquiptList.isEmpty()) {
            equiptTestMethodService.getDao().insert(newEquiptList);
        }
    }
}
