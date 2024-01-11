package com.sunwayworld.emc.module.mdm.basemdm.equiptcategory.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.sunwayworld.emc.module.mdm.basemdm.equiptcategory.bean.EmcEquiptCategoryBean;
import com.sunwayworld.emc.module.mdm.basemdm.equiptcategory.bean.EmcEquiptMaintBean;
import com.sunwayworld.emc.module.mdm.basemdm.equiptcategory.dao.EmcEquiptCategoryDao;
import com.sunwayworld.emc.module.mdm.basemdm.equiptcategory.service.EmcEquiptCategoryService;
import com.sunwayworld.emc.module.mdm.basemdm.equiptcategory.service.EmcEquiptMaintService;
import com.sunwayworld.emc.module.mdm.org.bean.EmcOrgPermissionBean;
import com.sunwayworld.emc.module.mdm.org.service.EmcOrgPermissionService;
import com.sunwayworld.framework.at.annotation.AuditTrailEntry;
import com.sunwayworld.framework.at.annotation.AuditTrailType;
import com.sunwayworld.framework.context.ApplicationContextHelper;
import com.sunwayworld.framework.data.page.Page;
import com.sunwayworld.framework.mybatis.mapper.MatchPattern;
import com.sunwayworld.framework.mybatis.mapper.SearchFilter;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;

/**
 * @author magh@sunwayworld.com 2019年9月4日
 */
@Repository
public class EmcEquiptCategoryServiceImpl implements EmcEquiptCategoryService {

    @Autowired
    private EmcEquiptCategoryDao emcEquiptCategoryDao;
    @Autowired
    private EmcEquiptMaintService equiptMaintService;
    @Autowired
    private EmcOrgPermissionService orgPermissionService;

    @SuppressWarnings("unchecked")
    @Override
    public EmcEquiptCategoryDao getDao() {
        return emcEquiptCategoryDao;
    }

    @Override
    @Transactional
    @AuditTrailEntry(AuditTrailType.INSERT)
    public Long insert(RestJsonWrapperBean wrapper) {
        EmcEquiptCategoryBean emcEquiptCategoryBean = wrapper.parseUnique(EmcEquiptCategoryBean.class);
        Long id = ApplicationContextHelper.getNextIdentity();
        emcEquiptCategoryBean.setId(id);
        getDao().insert(emcEquiptCategoryBean);
        return emcEquiptCategoryBean.getId();
    }

    @Override
    @Transactional
    @AuditTrailEntry(AuditTrailType.DELETE)
    public void delete(RestJsonWrapperBean wrapper) {
        List<EmcEquiptCategoryBean> emcEquiptCategoryBeanList = wrapper.parse(EmcEquiptCategoryBean.class);
        List<EmcOrgPermissionBean> deletedOrgPermissionList = new ArrayList<EmcOrgPermissionBean>();
        emcEquiptCategoryBeanList.forEach(b -> {
            EmcOrgPermissionBean orgPermission = new EmcOrgPermissionBean();
            orgPermission.setTargetId(b.getId().toString());
            orgPermission.setPermissionTypeId(getDao().getTable());
            deletedOrgPermissionList.add(orgPermission);
        });
        orgPermissionService.getDao().deleteBy(deletedOrgPermissionList, "permissionTypeId", "targetId");
        EmcEquiptCategoryService.super.delete(wrapper);
    }

    @Override
    public Page<EmcEquiptMaintBean> selectEquiptMaintPaginationByEquiptCategoryId(Long id,
            RestJsonWrapperBean wrapper) {
        return equiptMaintService.selectPaginationByFilter(SearchFilter.instance().match("EQUIPTCATEGORYID", id).filter(MatchPattern.EQ), wrapper);
    }

    @Override
    @Transactional
    public Long insertEquiptMaint(Long id, RestJsonWrapperBean wrapper) {
        return equiptMaintService.insert(id, wrapper);
    }

    @Override
    @Transactional
    public void deleteEquiptMaint(Long id, RestJsonWrapperBean wrapper) {
        equiptMaintService.delete(wrapper);
    }

}
