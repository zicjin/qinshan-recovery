package com.sunwayworld.emc.module.mdm.basemdm.mtlcategory.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.sunwayworld.emc.module.mdm.basemdm.mtlcategory.bean.EmcMtlCategoryBean;
import com.sunwayworld.emc.module.mdm.basemdm.mtlcategory.dao.EmcMtlCategoryDao;
import com.sunwayworld.emc.module.mdm.basemdm.mtlcategory.service.EmcMtlCategoryService;
import com.sunwayworld.emc.module.mdm.org.bean.EmcOrgPermissionBean;
import com.sunwayworld.emc.module.mdm.org.service.EmcOrgPermissionService;
import com.sunwayworld.framework.at.annotation.AuditTrailEntry;
import com.sunwayworld.framework.at.annotation.AuditTrailType;
import com.sunwayworld.framework.context.ApplicationContextHelper;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;

/**
 * @author magh@sunwayworld.com
 * @date 2019-09-16
 */

@Repository
public class EmcMtlCategoryServiceImpl implements EmcMtlCategoryService {
    @Autowired
    private EmcMtlCategoryDao emcMtlCategoryDao;

    @Autowired
    private EmcOrgPermissionService orgPermissionService;

    @Override
    @SuppressWarnings("unchecked")
    public EmcMtlCategoryDao getDao() {
        return emcMtlCategoryDao;
    }

    @Override
    @Transactional
    @AuditTrailEntry(AuditTrailType.INSERT)
    public Long insert(RestJsonWrapperBean jsonWrapper) {
        EmcMtlCategoryBean mtlCategory = jsonWrapper.parseUnique(EmcMtlCategoryBean.class);
        mtlCategory.setId(ApplicationContextHelper.getNextIdentity());
        getDao().insert(mtlCategory);
        return mtlCategory.getId();
    }

    @Override
    @Transactional
    @AuditTrailEntry(AuditTrailType.DELETE)
    public void delete(RestJsonWrapperBean wrapper) {
        List<EmcMtlCategoryBean> emcMtlCategoryBeanList = wrapper.parse(EmcMtlCategoryBean.class);
        List<EmcOrgPermissionBean> deletedOrgPermissionList = new ArrayList<EmcOrgPermissionBean>();
        emcMtlCategoryBeanList.forEach(b -> {
            EmcOrgPermissionBean orgPermission = new EmcOrgPermissionBean();
            orgPermission.setTargetId(b.getId().toString());
            orgPermission.setPermissionTypeId(getDao().getTable());
            deletedOrgPermissionList.add(orgPermission);
        });
        orgPermissionService.getDao().deleteBy(deletedOrgPermissionList, "permissionTypeId", "targetId");
        EmcMtlCategoryService.super.delete(wrapper);
    }

}
