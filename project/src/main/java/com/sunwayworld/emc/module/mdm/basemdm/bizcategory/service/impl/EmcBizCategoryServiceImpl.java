package com.sunwayworld.emc.module.mdm.basemdm.bizcategory.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.sunwayworld.emc.module.mdm.basemdm.bizcategory.bean.EmcBizCategoryBean;
import com.sunwayworld.emc.module.mdm.basemdm.bizcategory.bean.EmcBizCategoryProjectBean;
import com.sunwayworld.emc.module.mdm.basemdm.bizcategory.dao.EmcBizCategoryDao;
import com.sunwayworld.emc.module.mdm.basemdm.bizcategory.service.EmcBizCategoryProjectService;
import com.sunwayworld.emc.module.mdm.basemdm.bizcategory.service.EmcBizCategoryService;
import com.sunwayworld.framework.at.annotation.AuditTrailEntry;
import com.sunwayworld.framework.at.annotation.AuditTrailType;
import com.sunwayworld.framework.context.ApplicationContextHelper;
import com.sunwayworld.framework.data.page.Page;
import com.sunwayworld.framework.mybatis.mapper.MatchPattern;
import com.sunwayworld.framework.mybatis.mapper.SearchFilter;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.spring.annotation.GikamBean;

@Repository
@GikamBean
public class EmcBizCategoryServiceImpl implements EmcBizCategoryService {

    @Autowired
    private EmcBizCategoryDao emcBizCategoryDao;
    
    @Autowired
    private EmcBizCategoryProjectService emcBizCategoryProjectService;

    @SuppressWarnings("unchecked")
    @Override
    public EmcBizCategoryDao getDao() {
        return emcBizCategoryDao;
    }

    @Override
    @Transactional
    @AuditTrailEntry(AuditTrailType.INSERT)
    public Long insert(RestJsonWrapperBean wrapper) {
        EmcBizCategoryBean emcBizCategoryBean = wrapper.parseUnique(EmcBizCategoryBean.class);
        Long id = ApplicationContextHelper.getNextIdentity();
        emcBizCategoryBean.setId(id);
        getDao().insert(emcBizCategoryBean);
        return id;
    }

    @Override
    @Transactional
    @AuditTrailEntry(AuditTrailType.INSERT)
    public void insertProject(Long id, RestJsonWrapperBean wrapper) {
        EmcBizCategoryProjectBean emcBizCategoryProjectBean = wrapper.parseUnique(EmcBizCategoryProjectBean.class);
        emcBizCategoryProjectBean.setId(ApplicationContextHelper.getNextIdentity());
        emcBizCategoryProjectBean.setBizCategoryId(id);
        emcBizCategoryProjectService.getDao().insert(emcBizCategoryProjectBean);
    }

    @Override
    public Page<EmcBizCategoryProjectBean> selectBizCategoryProjectPaginationByBizCategoryId(Long id, RestJsonWrapperBean wrapper) {
        return emcBizCategoryProjectService.selectPaginationByFilter(SearchFilter.instance().match("BIZCATEGORYID", id).filter(MatchPattern.EQ), wrapper);
    }

    @Override
    @Transactional
    @AuditTrailEntry(AuditTrailType.DELETE)
    public void deleteProject(Long id, RestJsonWrapperBean wrapper) {
        emcBizCategoryProjectService.delete(wrapper);
    }

    @Override
    public Page<EmcBizCategoryProjectBean> selectBizCategoryProjectChoosablePagination(RestJsonWrapperBean wrapper) {
        return emcBizCategoryProjectService.selectChoosablePagination(wrapper);
    }

    @Override
    public List<EmcBizCategoryProjectBean> selectProjectSelectableList() {
        return emcBizCategoryProjectService.selectSelectableList();
    }

}
