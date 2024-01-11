package com.sunwayworld.emc.module.mdm.bizmdm.comparison.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.sunwayworld.emc.module.mdm.bizmdm.comparison.bean.EmcComparisonBean;
import com.sunwayworld.emc.module.mdm.bizmdm.comparison.bean.EmcComparisonResultBean;
import com.sunwayworld.emc.module.mdm.bizmdm.comparison.dao.EmcComparisonDao;
import com.sunwayworld.emc.module.mdm.bizmdm.comparison.service.EmcComparisonResultService;
import com.sunwayworld.emc.module.mdm.bizmdm.comparison.service.EmcComparisonService;
import com.sunwayworld.framework.at.annotation.AuditTrailEntry;
import com.sunwayworld.framework.at.annotation.AuditTrailType;
import com.sunwayworld.framework.context.ApplicationContextHelper;
import com.sunwayworld.framework.data.page.Page;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.utils.NumberUtils;
import com.sunwayworld.framework.utils.StringUtils;

/**
* @author zhengxy@sunwayworld.com 2020年08月17日
*/
@Repository
public class EmcComparisonServiceImpl implements EmcComparisonService {

    @Autowired
    private EmcComparisonResultService comparisonResultService;

    @Autowired
    private EmcComparisonDao emcComparisonDao;

    @Override
    @SuppressWarnings("unchecked")
    public EmcComparisonDao getDao() {
        return emcComparisonDao;
    }

    @Override
    @Transactional
    @AuditTrailEntry(AuditTrailType.INSERT)
    public Long insert(RestJsonWrapperBean wrapperBean) {
        EmcComparisonBean comparison = wrapperBean.parseUnique(EmcComparisonBean.class);
        String ids = comparison.getExt$Item("ids");
        String[] testIds = StringUtils.split(ids, ",");
        String[] testNames = StringUtils.split(comparison.getTestName(), ",");

        List<EmcComparisonBean> comparisonList = new ArrayList<>();
        IntStream.range(0, testIds.length).forEach(index -> {
            EmcComparisonBean newComparison = new EmcComparisonBean();
            newComparison.setId(ApplicationContextHelper.getNextIdentity());
            newComparison.setTestId(NumberUtils.parseLong(testIds[index]));
            newComparison.setTestName(testNames[index]);
            newComparison.setMonitorPointId(comparison.getMonitorPointId());
            comparisonList.add(newComparison);
        });
        getDao().insert(comparisonList);

        return comparisonList.isEmpty() ? 0L : comparisonList.get(0).getId();
    }

    @Override
    public Page<EmcComparisonResultBean> selectResultPagination(RestJsonWrapperBean wrapper) {
        return comparisonResultService.selectPagination(wrapper);
    }

    @Override
    public void insertResultByFolder(Long folderId) {
        comparisonResultService.insertResultByFolder(folderId);
    }
}
