package com.sunwayworld.emc.module.mdm.bizmdm.test.service.impl;

import java.util.List;
import java.util.stream.Collectors;

import com.sunwayworld.framework.spring.annotation.GikamBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.sunwayworld.emc.module.bp.project.project.bean.EmcOrderItemGroupBean;
import com.sunwayworld.emc.module.bp.project.project.service.EmcOrderItemGroupService;
import com.sunwayworld.emc.module.bp.project.projscheme.bean.EmcProjOrderBean;
import com.sunwayworld.emc.module.bp.project.projscheme.service.EmcProjOrderService;
import com.sunwayworld.emc.module.mdm.bizmdm.test.bean.EmcTestBean;
import com.sunwayworld.emc.module.mdm.bizmdm.test.dao.EmcTestDao;
import com.sunwayworld.emc.module.mdm.bizmdm.test.service.EmcTestService;
import com.sunwayworld.emc.module.mdm.bizmdm.test.validator.EmcTestDeleteValidator;
import com.sunwayworld.framework.at.annotation.AuditTrailEntry;
import com.sunwayworld.framework.at.annotation.AuditTrailType;
import com.sunwayworld.framework.context.ApplicationContextHelper;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.validator.data.annotation.ValidateDataWith;

/**
 * @author magh@sunwayworld.com
 * @date 2019-10-18
 */

@GikamBean
@Repository
public class EmcTestServiceImpl implements EmcTestService {
    @Autowired
    private EmcTestDao emcTestDao;

    @Autowired
    @Lazy
    private EmcOrderItemGroupService orderItemGroupService;

    @Autowired
    @Lazy
    private EmcProjOrderService projOrderService;

    @Override
    @SuppressWarnings("unchecked")
    public EmcTestDao getDao() {
        return emcTestDao;
    }

    @Override
    @Transactional
    @AuditTrailEntry(AuditTrailType.INSERT)
    public Long insert(RestJsonWrapperBean jsonWrapper) {
        EmcTestBean emcTest = jsonWrapper.parseUnique(EmcTestBean.class);
        emcTest.setId(ApplicationContextHelper.getNextSequenceNumber(getDao().getTable()));
        getDao().insert(emcTest);
        return emcTest.getId();
    }
    
    @Override
    @ValidateDataWith(EmcTestDeleteValidator.class)
    public void delete(RestJsonWrapperBean jsonWrapper) {
        EmcTestService.super.delete(jsonWrapper);
    }

    @Override
    public List<Long> checkUsed(RestJsonWrapperBean wrapper) {
        List<Long> testIdList = wrapper.parseId(Long.class);

        List<EmcOrderItemGroupBean> orderItemGroupList = orderItemGroupService.getDao().selectListByOneColumnValues(testIdList, "TESTID");

        if (orderItemGroupList.isEmpty()) {
            List<EmcProjOrderBean> projOrderList = projOrderService.getDao().selectListByOneColumnValues(testIdList, "TESTID");

            return projOrderList.stream().map(p -> p.getId()).collect(Collectors.toList());
        }

        return orderItemGroupList.stream().map(g -> g.getId()).collect(Collectors.toList());
    }
}
