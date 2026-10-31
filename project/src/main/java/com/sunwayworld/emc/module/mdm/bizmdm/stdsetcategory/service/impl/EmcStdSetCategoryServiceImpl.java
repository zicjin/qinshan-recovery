package com.sunwayworld.emc.module.mdm.bizmdm.stdsetcategory.service.impl;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.cxf.common.util.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.sunwayworld.emc.framework.constant.EmcConstant;
import com.sunwayworld.emc.module.bp.bp.bean.EmcOrderTaskResultBean;
import com.sunwayworld.emc.module.mdm.basemdm.qccategory.bean.EmcQcCategoryBean;
import com.sunwayworld.emc.module.mdm.basemdm.qccategory.service.EmcQcCategoryService;
import com.sunwayworld.emc.module.mdm.bizmdm.stdsetcategory.bean.EmcFieldSetBean;
import com.sunwayworld.emc.module.mdm.bizmdm.stdsetcategory.bean.EmcStandardSetBean;
import com.sunwayworld.emc.module.mdm.bizmdm.stdsetcategory.bean.EmcStdSetCategoryBean;
import com.sunwayworld.emc.module.mdm.bizmdm.stdsetcategory.dao.EmcStdSetCategoryDao;
import com.sunwayworld.emc.module.mdm.bizmdm.stdsetcategory.service.EmcFieldSetService;
import com.sunwayworld.emc.module.mdm.bizmdm.stdsetcategory.service.EmcStandardSetService;
import com.sunwayworld.emc.module.mdm.bizmdm.stdsetcategory.service.EmcStdSetCategoryService;
import com.sunwayworld.framework.at.annotation.AuditTrailEntry;
import com.sunwayworld.framework.at.annotation.AuditTrailType;
import com.sunwayworld.framework.beans.BeanPropertyDescriptor;
import com.sunwayworld.framework.beans.BeanPropertyHelper;
import com.sunwayworld.framework.context.ApplicationContextHelper;
import com.sunwayworld.framework.data.page.Page;
import com.sunwayworld.framework.data.page.Pagination;
import com.sunwayworld.framework.mybatis.mapper.MatchPattern;
import com.sunwayworld.framework.mybatis.mapper.SearchFilter;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.utils.NumberUtils;

@Repository
public class EmcStdSetCategoryServiceImpl implements EmcStdSetCategoryService {

    @Autowired
    private EmcStdSetCategoryDao stdSetCategoryDao;

    @Autowired
    private EmcStandardSetService standardSetService;

    @Autowired
    private EmcFieldSetService fieldSetService;

    @Autowired
    @Lazy
    private EmcQcCategoryService qcCategoryService;

    @Override
    @SuppressWarnings("unchecked")
    public EmcStdSetCategoryDao getDao() {
        return stdSetCategoryDao;
    }

    @Override
    @Transactional
    @AuditTrailEntry(AuditTrailType.INSERT)
    public Long insert(RestJsonWrapperBean jsonWrapper) {
        EmcStdSetCategoryBean emcStdSetCategory = jsonWrapper.parseUnique(EmcStdSetCategoryBean.class);
        emcStdSetCategory.setId(ApplicationContextHelper.getNextIdentity());
        getDao().insert(emcStdSetCategory);
        return emcStdSetCategory.getId();
    }

    @Override
    @Transactional
    @AuditTrailEntry(AuditTrailType.INSERT)
    public Long insertStandardSet(Long id, RestJsonWrapperBean wrapper) {
        EmcStandardSetBean emcStandardSet = wrapper.parseUnique(EmcStandardSetBean.class);
        emcStandardSet.setId(ApplicationContextHelper.getNextIdentity());
        emcStandardSet.setStandSetCategoryId(id);
        standardSetService.getDao().insert(emcStandardSet);
        return emcStandardSet.getId();
    }

    @Override
    public Page<EmcStandardSetBean> selectStandardSetPaginationById(Long id, RestJsonWrapperBean wrapper) {
        return standardSetService.selectPaginationByFilter(SearchFilter.instance().match("STANDSETCATEGORYID", id).filter(MatchPattern.EQ), wrapper);

    }

    @Override
    @Transactional
    @AuditTrailEntry(AuditTrailType.DELETE)
    public void deleteStandardSet(RestJsonWrapperBean wrapper) {
        standardSetService.delete(wrapper);

    }

    @Override
    @Transactional
    @AuditTrailEntry(AuditTrailType.INSERT)
    public Long insertFieldSet(Long standardSetId, RestJsonWrapperBean wrapper) {
        List<EmcFieldSetBean> emcFieldSetList = wrapper.parse(EmcFieldSetBean.class);
        emcFieldSetList.stream().forEach(e -> {
            e.setId(ApplicationContextHelper.getNextIdentity());
            e.setStandardSetId(standardSetId);
        });
        List<EmcFieldSetBean> oldEmcFieldSetList = fieldSetService.selectListByFilter(SearchFilter.instance().match("STANDARDSETID", standardSetId).filter(MatchPattern.EQ), wrapper);
        List<EmcFieldSetBean> newEmcFieldSetList = emcFieldSetList.stream().filter(e -> {
            return !oldEmcFieldSetList.contains(e);
        }).collect(Collectors.toList());

        if (newEmcFieldSetList.isEmpty()) {
            return EmcConstant.ONE;
        }
        fieldSetService.getDao().insert(newEmcFieldSetList);
        return newEmcFieldSetList.get(0).getId();
    }

    @Override
    public Page<EmcFieldSetBean> selectFieldSetPaginationByStandardSetId(Long standardSetId, RestJsonWrapperBean wrapper) {
        return fieldSetService.selectPaginationByFilter(SearchFilter.instance().match("STANDARDSETID", standardSetId).filter(MatchPattern.EQ), wrapper);
    }

    @Override
    @Transactional
    @AuditTrailEntry(AuditTrailType.DELETE)
    public void deleteFieldSet(RestJsonWrapperBean wrapper) {
        fieldSetService.delete(wrapper);
    }

    @Override
    public Page<BeanPropertyDescriptor> selectFieldInfoPagination(RestJsonWrapperBean wrapper) {

        List<BeanPropertyDescriptor> beanPropertyDescriptors = BeanPropertyHelper.getBeanPropertyDescriptorList(EmcOrderTaskResultBean.class);

        return new Pagination<>(beanPropertyDescriptors);
    }

    @Override
    public List<EmcStandardSetBean> selectStandardSetCategorySelectableList() {
        return standardSetService.selectSelectableList();
    }

    @Override
    public EmcStandardSetBean selectStandardSetDetail(Long standSetId) {
        return standardSetService.selectDetail(standSetId);
    }

    @Override
    public List<EmcFieldSetBean> selectFieldSetList(RestJsonWrapperBean wrapper) {
        // 根据是否有qcCategoryId判断是普通样还是qc样
        String qcCategoryId = wrapper.getFilterValue("qcCategoryId");
        // qc类型的qc组是标准设置表的主键
        if (!StringUtils.isEmpty(qcCategoryId)) {
            EmcQcCategoryBean qcCategory = qcCategoryService.getDao().selectByIdIfPresent(NumberUtils.parseLong(qcCategoryId));
            if (qcCategory != null) {
                Long qcGroup = qcCategory.getQcGroup();
                return fieldSetService.getDao().selectListByOneColumnValue(qcGroup, "STANDARDSETID");
            }
        }
        // 查询出标准设置名称为QCSCHEMAS的数据
        EmcStdSetCategoryBean categoryFilter = new EmcStdSetCategoryBean();
        categoryFilter.setStandardSet("QCSCHEMAS");
        EmcStdSetCategoryBean stdSetCategory = getDao().selectFirstIfPresent(categoryFilter);
        if (stdSetCategory == null) {
            return Collections.emptyList();
        }

        EmcStandardSetBean setFilter = new EmcStandardSetBean();
        setFilter.setStandSetCategoryId(stdSetCategory.getId());
        setFilter.setSetName(wrapper.getFilterValue("qcCategoryCode"));
        EmcStandardSetBean standardSet = standardSetService.getDao().selectFirstIfPresent(setFilter);
        if (standardSet == null) {
            return Collections.emptyList();
        }

        return fieldSetService.getDao().selectListByOneColumnValue(standardSet.getId(), "STANDARDSETID");
    }

}
