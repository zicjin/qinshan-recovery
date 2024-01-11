package com.sunwayworld.emc.module.mdm.contract.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.sunwayworld.emc.module.mdm.contract.bean.EmcExpenseCategoryBean;
import com.sunwayworld.emc.module.mdm.contract.bean.EmcMonitorItemBean;
import com.sunwayworld.emc.module.mdm.contract.dao.EmcExpenseCategoryDao;
import com.sunwayworld.emc.module.mdm.contract.service.EmcExpenseCategoryService;
import com.sunwayworld.emc.module.mdm.contract.service.EmcMonitorItemService;
import com.sunwayworld.framework.at.annotation.AuditTrailEntry;
import com.sunwayworld.framework.at.annotation.AuditTrailType;
import com.sunwayworld.framework.context.ApplicationContextHelper;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.spring.annotation.GikamBean;

@Repository
@GikamBean
public class EmcExpenseCategoryServiceImpl implements EmcExpenseCategoryService {

    @Autowired
    private EmcExpenseCategoryDao emcExpenseCategoryDao;

    @Autowired
    private EmcMonitorItemService emcMonitorItemService;

    @Override
    @SuppressWarnings("unchecked")
    public EmcExpenseCategoryDao getDao() {
        return emcExpenseCategoryDao;
    }

    @Override
    @Transactional
    @AuditTrailEntry(AuditTrailType.INSERT)
    public Long insert(Long contractId, RestJsonWrapperBean jsonWrapper) {
        EmcExpenseCategoryBean emcExpenseCategory = new EmcExpenseCategoryBean();
        emcExpenseCategory.setId(ApplicationContextHelper.getNextIdentity());
        emcExpenseCategory.setContractId(contractId);
        getDao().insert(emcExpenseCategory);
        return emcExpenseCategory.getId();
    }

    @Override
    @Transactional
    @AuditTrailEntry(AuditTrailType.INSERT)
    public void copyExpenseCategory(RestJsonWrapperBean wrapper) {
        List<EmcExpenseCategoryBean> expenseCategoryList = wrapper.parse(EmcExpenseCategoryBean.class);
        if (!expenseCategoryList.isEmpty()) {
            List<Long> idList = expenseCategoryList.stream().map(e -> e.getId()).collect(Collectors.toList());
            List<EmcExpenseCategoryBean> allExpenseCategoryList = getDao().selectListByIds(idList);
            List<EmcMonitorItemBean> allMonitorItemList = emcMonitorItemService.getDao().selectListByOneColumnValues(idList, "EXPENSECATEGORYID");

            List<EmcExpenseCategoryBean> insertExpenseCategoryList = new ArrayList<>();
            List<EmcMonitorItemBean> insertMonitorItemList = new ArrayList<>();
            allExpenseCategoryList.stream().forEach(e -> {
                Long oldId = e.getId();
                e.setId(ApplicationContextHelper.getNextIdentity());
                insertExpenseCategoryList.add(e);
                allMonitorItemList.stream().forEach(m -> {
                    if (oldId.equals(m.getExpenseCategoryId())) {
                        m.setId(ApplicationContextHelper.getNextIdentity());
                        m.setExpenseCategoryId(e.getId());
                        insertMonitorItemList.add(m);
                    }
                });
            });

            getDao().insert(insertExpenseCategoryList);
            emcMonitorItemService.getDao().insert(insertMonitorItemList);
        }

    }

}
