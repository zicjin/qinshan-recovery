package com.sunwayworld.emc.module.mdm.basemdm.filecategory.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.sunwayworld.emc.module.mdm.basemdm.filecategory.bean.EmcFileCategoryBean;
import com.sunwayworld.emc.module.mdm.basemdm.filecategory.dao.EmcFileCategoryDao;
import com.sunwayworld.emc.module.mdm.basemdm.filecategory.service.EmcFileCategoryService;
import com.sunwayworld.emc.module.mdm.org.bean.EmcOrgPermissionBean;
import com.sunwayworld.emc.module.mdm.org.service.EmcOrgPermissionService;
import com.sunwayworld.framework.at.annotation.AuditTrailEntry;
import com.sunwayworld.framework.at.annotation.AuditTrailType;
import com.sunwayworld.framework.context.ApplicationContextHelper;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;

/**
 * @author zhengxy@sunwayworld.com 2019年10月9日
 */
@Repository
public class EmcFileCategoryServiceImpl implements EmcFileCategoryService {

    @Autowired
    private EmcFileCategoryDao emcFileCategoryDao;

    @Autowired
    private EmcOrgPermissionService orgPermissionService;

    @Override
    @SuppressWarnings("unchecked")
    public EmcFileCategoryDao getDao() {
        return emcFileCategoryDao;
    }

    @Override
    @Transactional
    @AuditTrailEntry(AuditTrailType.INSERT)
    public Long insert(RestJsonWrapperBean wrapper) {
        EmcFileCategoryBean emcFileCategoryBean = wrapper.parseUnique(EmcFileCategoryBean.class);
        Long id = ApplicationContextHelper.getNextIdentity();
        emcFileCategoryBean.setId(id);
        getDao().insert(emcFileCategoryBean);
        return emcFileCategoryBean.getId();
    }

    @Override
    @Transactional
    @AuditTrailEntry(AuditTrailType.DELETE)
    public void delete(RestJsonWrapperBean wrapper) {
        List<EmcFileCategoryBean> emcFileCategoryBeanList = wrapper.parse(EmcFileCategoryBean.class);
        List<EmcOrgPermissionBean> deletedOrgPermissionList = new ArrayList<EmcOrgPermissionBean>();
        emcFileCategoryBeanList.forEach(b -> {
            EmcOrgPermissionBean orgPermission = new EmcOrgPermissionBean();
            orgPermission.setTargetId(b.getId().toString());
            orgPermission.setPermissionTypeId(getDao().getTable());
            deletedOrgPermissionList.add(orgPermission);
        });
        orgPermissionService.getDao().deleteBy(deletedOrgPermissionList, "permissionTypeId", "targetId");
        EmcFileCategoryService.super.delete(wrapper);
    }
}
