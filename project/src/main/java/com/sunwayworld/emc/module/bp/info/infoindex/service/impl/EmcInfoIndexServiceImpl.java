package com.sunwayworld.emc.module.bp.info.infoindex.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.sunwayworld.emc.module.bp.info.infoindex.bean.EmcCategoryIndexBean;
import com.sunwayworld.emc.module.bp.info.infoindex.bean.EmcCategorySectionBean;
import com.sunwayworld.emc.module.bp.info.infoindex.bean.EmcInfoIndexBean;
import com.sunwayworld.emc.module.bp.info.infoindex.bean.EmcInfoIndexCategoryBean;
import com.sunwayworld.emc.module.bp.info.infoindex.dao.EmcInfoIndexDao;
import com.sunwayworld.emc.module.bp.info.infoindex.service.EmcCategoryIndexService;
import com.sunwayworld.emc.module.bp.info.infoindex.service.EmcCategorySectionService;
import com.sunwayworld.emc.module.bp.info.infoindex.service.EmcInfoIndexCategoryService;
import com.sunwayworld.emc.module.bp.info.infoindex.service.EmcInfoIndexService;
import com.sunwayworld.framework.at.annotation.AuditTrailEntry;
import com.sunwayworld.framework.at.annotation.AuditTrailType;
import com.sunwayworld.framework.context.ApplicationContextHelper;
import com.sunwayworld.framework.data.page.Page;
import com.sunwayworld.framework.log.annotation.Log;
import com.sunwayworld.framework.log.annotation.LogType;
import com.sunwayworld.framework.mybatis.mapper.MatchPattern;
import com.sunwayworld.framework.mybatis.mapper.SearchFilter;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.spring.annotation.GikamBean;

/**
 * @author zhangft@sunwayworld.com
 * @date 2019-11-21
 */

@Repository
@GikamBean
public class EmcInfoIndexServiceImpl implements EmcInfoIndexService {
    @Autowired
    private EmcInfoIndexDao infoIndexDao;

    @Autowired
    private EmcInfoIndexCategoryService infoIndexCategoryService;

    @Autowired
    private EmcCategoryIndexService categoryIndexService;

    @Autowired
    private EmcCategorySectionService categorySectionService;

    @Override
    @SuppressWarnings("unchecked")
    public EmcInfoIndexDao getDao() {
        return infoIndexDao;
    }

    @Override
    @Log(value = "新增评价标准", type = LogType.SELECT)
    @Transactional
    @AuditTrailEntry(AuditTrailType.INSERT)
    public Long insert(RestJsonWrapperBean jsonWrapper) {
        EmcInfoIndexBean infoIndex = jsonWrapper.parseUnique(EmcInfoIndexBean.class);
        infoIndex.setId(ApplicationContextHelper.getNextIdentity());
        getDao().insert(infoIndex);
        return infoIndex.getId();
    }

    @Override
    @Log(value = "查询评价标准分类", type = LogType.SELECT)
    public Page<EmcInfoIndexCategoryBean> selectIndexCategoryPaginationByIndexId(Long indexId, RestJsonWrapperBean wrapper) {
        return infoIndexCategoryService.selectPaginationByFilter(SearchFilter.instance().match("INDEXID", indexId).filter(MatchPattern.EQ), wrapper);
    }

    @Override
    @Log(value = "新增评价标准分类", type = LogType.INSERT)
    @Transactional
    @AuditTrailEntry(AuditTrailType.INSERT)
    public Long insertIndexCategory(Long indexId, RestJsonWrapperBean wrapper) {
        return infoIndexCategoryService.insert(indexId, wrapper);
    }

    @Override
    @Log(value = "删除评价标准分类", type = LogType.DELETE)
    @Transactional
    @AuditTrailEntry(AuditTrailType.DELETE)
    public void deleteIndexCategory(RestJsonWrapperBean wrapper) {
        infoIndexCategoryService.delete(wrapper);
    }

    @Override
    @Log(value = "复制评价标准分类", type = LogType.INSERT)
    @Transactional
    @AuditTrailEntry(AuditTrailType.INSERT)
    public EmcInfoIndexCategoryBean copyIndexCategory(Long categoryId) {
        EmcInfoIndexCategoryBean toIndexCategory = infoIndexCategoryService.copy(categoryId);
        categoryIndexService.copy(categoryId, toIndexCategory.getId());
        categorySectionService.copy(categoryId, toIndexCategory.getId());
        return toIndexCategory;
    }

    @Override
    @Log(value = "根据分类查询分类指标值", type = LogType.SELECT)
    public Page<EmcCategoryIndexBean> selectCategoryIndexPaginationByCategoryId(Long categoryId, RestJsonWrapperBean wrapper) {
        return categoryIndexService.selectPaginationByFilter(SearchFilter.instance().match("INDEXCATEGORYID", categoryId).filter(MatchPattern.EQ), wrapper);
    }

    @Override
    @Log(value = "新增分类指标值", type = LogType.INSERT)
    @Transactional
    @AuditTrailEntry(AuditTrailType.INSERT)
    public Long insertCategoryIndex(Long categoryId, RestJsonWrapperBean wrapper) {
        return categoryIndexService.insert(categoryId, wrapper);
    }

    @Override
    @Log(value = "删除分类指标值", type = LogType.DELETE)
    @Transactional
    @AuditTrailEntry(AuditTrailType.DELETE)
    public void deleteCategoryIndex(RestJsonWrapperBean wrapper) {
        categoryIndexService.delete(wrapper);
    }

    @Override
    @Log(value = "根据分类查询分类断面属性", type = LogType.SELECT)
    public Page<EmcCategorySectionBean> selectCategorySectionPaginationByCategoryId(Long categoryId, RestJsonWrapperBean wrapper) {
        return categorySectionService.selectPaginationByFilter(SearchFilter.instance().match("INDEXCATEGORYID", categoryId).filter(MatchPattern.EQ), wrapper);
    }

    @Override
    @Log(value = "新增分类断面属性", type = LogType.INSERT)
    @Transactional
    @AuditTrailEntry(AuditTrailType.INSERT)
    public Long insertCategorySection(Long categoryId, RestJsonWrapperBean wrapper) {
        return categorySectionService.insert(categoryId, wrapper);
    }

    @Override
    @Log(value = "删除分类断面属性", type = LogType.DELETE)
    @Transactional
    @AuditTrailEntry(AuditTrailType.DELETE)
    public void deleteCategorySection(RestJsonWrapperBean wrapper) {
        categorySectionService.delete(wrapper);
    }
}
