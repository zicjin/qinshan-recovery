package com.sunwayworld.emc.module.bp.info.infoindex.service.impl;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.sunwayworld.emc.module.bp.info.infoindex.bean.EmcCategorySectionBean;
import com.sunwayworld.emc.module.bp.info.infoindex.dao.EmcCategorySectionDao;
import com.sunwayworld.emc.module.bp.info.infoindex.service.EmcCategorySectionService;
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
public class EmcCategorySectionServiceImpl implements EmcCategorySectionService {
    @Autowired
    private EmcCategorySectionDao categorySectionDao;

    @Override
    @SuppressWarnings("unchecked")
    public EmcCategorySectionDao getDao() {
        return categorySectionDao;
    }

    @Override
    @Log(value = "新增评价标准分类断面属性", type = LogType.INSERT)
    @Transactional
    @AuditTrailEntry(AuditTrailType.INSERT)
    public Long insert(Long categoryId, RestJsonWrapperBean jsonWrapper) {
        EmcCategorySectionBean categorySection = jsonWrapper.parseUnique(EmcCategorySectionBean.class);
        categorySection.setId(ApplicationContextHelper.getNextIdentity());
        categorySection.setIndexCategoryId(categoryId);
        getDao().insert(categorySection);
        return categorySection.getId();
    }

    @Override
    @Log(value = "复制评价标准分类断面属性", type = LogType.INSERT)
    @Transactional
    @AuditTrailEntry(AuditTrailType.INSERT)
    public List<EmcCategorySectionBean> copy(Long fromCategoryId, Long toCategoryId) {
        List<EmcCategorySectionBean> categorySectionList = getDao().selectListByOneColumnValue(fromCategoryId, "INDEXCATEGORYID", Order.asc("ID"));
        List<EmcCategorySectionBean> newCategorySectionList = categorySectionList.stream().map(categorySection -> {
            categorySection.setId(ApplicationContextHelper.getNextIdentity());
            categorySection.setIndexCategoryId(toCategoryId);
            return categorySection;
        }).collect(Collectors.toList());
        getDao().insert(newCategorySectionList);
        return newCategorySectionList;
    }
}
