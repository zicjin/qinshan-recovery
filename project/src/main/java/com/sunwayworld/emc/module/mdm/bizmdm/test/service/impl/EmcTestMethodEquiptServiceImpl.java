package com.sunwayworld.emc.module.mdm.bizmdm.test.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.sunwayworld.emc.module.mdm.bizmdm.test.bean.EmcTestMethodBean;
import com.sunwayworld.emc.module.mdm.bizmdm.test.dao.EmcTestMethodEquiptDao;
import com.sunwayworld.emc.module.mdm.bizmdm.test.service.EmcTestMethodEquiptService;
import com.sunwayworld.emc.module.mdm.bizmdm.test.service.EmcTestMethodService;
import com.sunwayworld.emc.module.mdm.equipt.equipt.bean.EmcEquiptTestMethodBean;
import com.sunwayworld.emc.module.mdm.equipt.equipt.service.EmcEquiptTestMethodService;
import com.sunwayworld.framework.at.annotation.AuditTrailEntry;
import com.sunwayworld.framework.at.annotation.AuditTrailType;
import com.sunwayworld.framework.constant.Constant;
import com.sunwayworld.framework.context.ApplicationContextHelper;
import com.sunwayworld.framework.context.LocalContextHelper;
import com.sunwayworld.framework.data.page.Page;
import com.sunwayworld.framework.mybatis.mapper.MatchPattern;
import com.sunwayworld.framework.mybatis.mapper.SearchFilter;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import com.sunwayworld.framework.utils.ObjectUtils;
import com.sunwayworld.framework.utils.StringUtils;

/**
 * @author yuanhh@sunwayworld.com
 * @date 2019-09-19
 */
@GikamBean
@Repository
public class EmcTestMethodEquiptServiceImpl implements EmcTestMethodEquiptService {
    @Autowired
    private EmcTestMethodEquiptDao emcTestMethodEquiptDao;

    @Autowired
    private EmcEquiptTestMethodService equiptTestMethodService;

    @Autowired
    @Lazy
    private EmcTestMethodService testMethodService;

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
            e.setAnalysisCategory("analysis");
        });
        equiptTestMethodService.getDao().insert(testEquiptList);
    }

    @Override
    @Transactional
    @AuditTrailEntry(AuditTrailType.UPDATE)
    public void setDefault(Long testMethodId, RestJsonWrapperBean wrapper) {
        List<EmcEquiptTestMethodBean> updateEquiptList = new ArrayList<EmcEquiptTestMethodBean>();
        EmcEquiptTestMethodBean filter = new EmcEquiptTestMethodBean();

        filter.setTestMethodId(testMethodId);
        filter.setDefaultFlag(Constant.YES);
        List<EmcEquiptTestMethodBean> equiptList = equiptTestMethodService.selectList(filter);

        if (equiptList.size() > 0) {
            EmcEquiptTestMethodBean equipt = equiptList.get(0);
            equipt.setDefaultFlag(Constant.NO);
            updateEquiptList.add(equipt);
        }

        EmcEquiptTestMethodBean defaultEquipt = wrapper.parseUnique(EmcEquiptTestMethodBean.class);
        defaultEquipt.setDefaultFlag(Constant.YES);
        updateEquiptList.add(defaultEquipt);

        equiptTestMethodService.getDao().update(updateEquiptList, "DEFAULTFLAG");

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
                newEquipt.setExt$Item("orgId", parentOrgId);
                newEquiptList.add(newEquipt);
            });
        });

        if (!newEquiptList.isEmpty()) {
            equiptTestMethodService.getDao().insert(newEquiptList);
        }
    }

    @Override
    public Page<EmcEquiptTestMethodBean> selectEquiptPagationByTestMethod(Long id, RestJsonWrapperBean wrapper) {
        return equiptTestMethodService.selectPaginationByFilter(SearchFilter.instance().match("TESTMETHODID", id).filter(MatchPattern.EQ), wrapper);
    }

    @Override
    public void deleteEquipt(RestJsonWrapperBean wrapper) {
        equiptTestMethodService.delete(wrapper);
    }
}
