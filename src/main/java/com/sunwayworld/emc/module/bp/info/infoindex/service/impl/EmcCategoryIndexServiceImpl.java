package com.sunwayworld.emc.module.bp.info.infoindex.service.impl;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.sunwayworld.emc.module.bp.info.infoindex.bean.EmcCategoryIndexBean;
import com.sunwayworld.emc.module.bp.info.infoindex.dao.EmcCategoryIndexDao;
import com.sunwayworld.emc.module.bp.info.infoindex.service.EmcCategoryIndexService;
import com.sunwayworld.framework.at.annotation.AuditTrailEntry;
import com.sunwayworld.framework.at.annotation.AuditTrailType;
import com.sunwayworld.framework.context.ApplicationContextHelper;
import com.sunwayworld.framework.database.sql.Order;
import com.sunwayworld.framework.log.annotation.Log;
import com.sunwayworld.framework.log.annotation.LogType;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.spring.annotation.GikamBean;

/**
 * @author zhangft@sunwayworld.com
 * @date 2019-11-21
 */

@Repository
@GikamBean
public class EmcCategoryIndexServiceImpl implements EmcCategoryIndexService {
    @Autowired
    private EmcCategoryIndexDao categoryIndexDao;

    @Override
    @SuppressWarnings("unchecked")
    public EmcCategoryIndexDao getDao() {
        return categoryIndexDao;
    }

    @Override
    @Log(value = "新增评价标准分类指标值", type = LogType.INSERT)
    @Transactional
    @AuditTrailEntry(AuditTrailType.INSERT)
    public Long insert(Long categoryId, RestJsonWrapperBean jsonWrapper) {
        EmcCategoryIndexBean categoryIndex = jsonWrapper.parseUnique(EmcCategoryIndexBean.class);
        categoryIndex.setId(ApplicationContextHelper.getNextIdentity());
        categoryIndex.setIndexCategoryId(categoryId);
        getDao().insert(categoryIndex);
        return categoryIndex.getId();
    }

    @Override
    @Log(value = "复制评价标准分类指标值", type = LogType.INSERT)
    @Transactional
    @AuditTrailEntry(AuditTrailType.INSERT)
    public List<EmcCategoryIndexBean> copy(Long fromCategoryId, Long toCategoryId) {
        List<EmcCategoryIndexBean> categoryIndexList = getDao().selectListByOneColumnValue(fromCategoryId, "INDEXCATEGORYID", Order.asc("ID"));
        List<EmcCategoryIndexBean> newCategoryIndexList = categoryIndexList.stream().map(categoryIndex -> {
            categoryIndex.setId(ApplicationContextHelper.getNextIdentity());
            categoryIndex.setIndexCategoryId(toCategoryId);
            return categoryIndex;
        }).collect(Collectors.toList());
        getDao().insert(newCategoryIndexList);
        return newCategoryIndexList;
    }
}
