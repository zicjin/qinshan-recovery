package com.sunwayworld.emc.module.mdm.bizmdm.comparison.service.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.TreeSet;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.sunwayworld.emc.module.bp.bp.bean.EmcOrderTaskBean;
import com.sunwayworld.emc.module.bp.bp.service.EmcOrderTaskService;
import com.sunwayworld.emc.module.mdm.bizmdm.comparison.bean.EmcComparisonResultBean;
import com.sunwayworld.emc.module.mdm.bizmdm.comparison.dao.EmcComparisonResultDao;
import com.sunwayworld.emc.module.mdm.bizmdm.comparison.service.EmcComparisonResultService;
import com.sunwayworld.framework.at.annotation.AuditTrailEntry;
import com.sunwayworld.framework.at.annotation.AuditTrailType;
import com.sunwayworld.framework.context.ApplicationContextHelper;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;

/**
* @author zhengxy@sunwayworld.com 2020年10月22日
*/
@Repository
public class EmcComparisonResultServiceImpl implements EmcComparisonResultService {

    @Autowired
    private EmcOrderTaskService orderTaskService;

    @Autowired
    private EmcComparisonResultDao emcComparisonResultDao;

    @Override
    @SuppressWarnings("unchecked")
    public EmcComparisonResultDao getDao() {
        return emcComparisonResultDao;
    }

    @Override
    @Transactional
    @AuditTrailEntry(AuditTrailType.INSERT)
    public Long insert(RestJsonWrapperBean wrapperBean) {
        EmcComparisonResultBean emcComparisonResultBean = wrapperBean.parseUnique(EmcComparisonResultBean.class);
        Long id = ApplicationContextHelper.getNextIdentity();
        emcComparisonResultBean.setId(id);
        getDao().insert(emcComparisonResultBean);
        return id;
    }

    @Override
    public void insertResultByFolder(Long folderId) {
        List<EmcComparisonResultBean> resultList = getDao().selectListByOneColumnValue(folderId, "FOLDERID", Collections.singletonList("ID"));
        if (!resultList.isEmpty()) {
            return;
        }

        List<EmcOrderTaskBean> orderTaskList = orderTaskService.getDao().selectListByOneColumnValue(folderId, "FOLDERID", Arrays.asList("ID", "PROJID", "TESTID", "TESTNAME"));
        List<EmcOrderTaskBean> uniqueTaskList = orderTaskList.stream().collect(Collectors.collectingAndThen(Collectors.toCollection(() -> new TreeSet<>(Comparator.comparing(EmcOrderTaskBean::getTestName))), ArrayList::new));

        List<String> testNameList = uniqueTaskList.stream().map(task -> task.getTestName().replaceAll("_比对.*", "")).distinct().collect(Collectors.toList());

        List<EmcComparisonResultBean> insertResultList = new ArrayList<>();
        testNameList.forEach(testName -> {
            EmcComparisonResultBean result = new EmcComparisonResultBean();
            result.setId(ApplicationContextHelper.getNextIdentity());
            result.setFolderId(folderId);
            result.setTestName(testName);
            insertResultList.add(result);
        });

        getDao().insert(insertResultList);
    }
}
