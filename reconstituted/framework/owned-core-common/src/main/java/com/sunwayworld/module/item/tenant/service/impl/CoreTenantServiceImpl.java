/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.module.item.tenant.service.impl;

import com.sunwayworld.framework.at.annotation.AuditTrailEntry;
import com.sunwayworld.framework.at.annotation.AuditTrailType;
import com.sunwayworld.framework.audit.aunnotation.Audit;
import com.sunwayworld.framework.context.ApplicationContextHelper;
import com.sunwayworld.framework.data.page.Page;
import com.sunwayworld.framework.mybatis.mapper.MatchPattern;
import com.sunwayworld.framework.mybatis.mapper.SearchFilter;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import com.sunwayworld.module.item.tenant.bean.CoreTenantBean;
import com.sunwayworld.module.item.tenant.bean.CoreTenantPermissionBean;
import com.sunwayworld.module.item.tenant.dao.CoreTenantDao;
import com.sunwayworld.module.item.tenant.service.CoreTenantPermissionService;
import com.sunwayworld.module.item.tenant.service.CoreTenantService;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@GikamBean
public class CoreTenantServiceImpl
implements CoreTenantService {
    @Autowired
    private CoreTenantDao coreTenantDao;
    @Autowired
    private CoreTenantPermissionService coreTenantPermissionService;

    @Override
    public CoreTenantDao getDao() {
        return this.coreTenantDao;
    }

    @Override
    @Transactional
    @AuditTrailEntry(value=AuditTrailType.INSERT)
    @Audit(value="AUDIT.INSERT")
    public Long insert(RestJsonWrapperBean jsonWrapper) {
        CoreTenantBean coreTenant = jsonWrapper.parseUnique(CoreTenantBean.class);
        coreTenant.setId(ApplicationContextHelper.getNextIdentity());
        this.getDao().insert(coreTenant);
        return coreTenant.getId();
    }

    @Override
    public Page<CoreTenantPermissionBean> selectPermissionPaginationByTenantId(Long id, RestJsonWrapperBean wrapper) {
        return this.coreTenantPermissionService.selectPaginationByFilter(SearchFilter.instance().match("TENANTID", id).filter(MatchPattern.EQ), wrapper);
    }

    @Override
    @Transactional
    @AuditTrailEntry(value=AuditTrailType.INSERT)
    @Audit(value="AUDIT.INSERT")
    public Long insertPermission(Long id, RestJsonWrapperBean wrapper) {
        List<CoreTenantPermissionBean> coreTenantPermissionList = wrapper.parse(CoreTenantPermissionBean.class);
        coreTenantPermissionList.forEach(e -> {
            e.setId(ApplicationContextHelper.getNextIdentity());
            e.setTenantId(id);
        });
        this.coreTenantPermissionService.getDao().insert(coreTenantPermissionList);
        return coreTenantPermissionList.get(0).getId();
    }

    @Override
    @Transactional
    @AuditTrailEntry(value=AuditTrailType.DELETE)
    @Audit(value="AUDIT.DELETE")
    public void deletePermission(Long id, RestJsonWrapperBean wrapper) {
        this.coreTenantPermissionService.delete(wrapper);
    }

    @Override
    public List<CoreTenantPermissionBean> selectLoginTenantList(RestJsonWrapperBean wrapper) {
        return this.coreTenantPermissionService.selectLoginTenantList(wrapper);
    }
}
