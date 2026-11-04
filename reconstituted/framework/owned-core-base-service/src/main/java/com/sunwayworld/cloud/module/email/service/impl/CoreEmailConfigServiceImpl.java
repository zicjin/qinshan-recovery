/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.email.service.impl;

import com.sunwayworld.cloud.module.email.bean.CoreEmailConfigBean;
import com.sunwayworld.cloud.module.email.bean.CoreEmailConfigCcRoleBean;
import com.sunwayworld.cloud.module.email.bean.CoreEmailConfigCcUserBean;
import com.sunwayworld.cloud.module.email.bean.CoreEmailConfigRecRoleBean;
import com.sunwayworld.cloud.module.email.bean.CoreEmailConfigReceipentBean;
import com.sunwayworld.cloud.module.email.persistent.dao.CoreEmailConfigDao;
import com.sunwayworld.cloud.module.email.service.CoreEmailConfigCcRoleService;
import com.sunwayworld.cloud.module.email.service.CoreEmailConfigCcUserService;
import com.sunwayworld.cloud.module.email.service.CoreEmailConfigRecRoleService;
import com.sunwayworld.cloud.module.email.service.CoreEmailConfigReceipentService;
import com.sunwayworld.cloud.module.email.service.CoreEmailConfigService;
import com.sunwayworld.framework.at.annotation.AuditTrailEntry;
import com.sunwayworld.framework.at.annotation.AuditTrailType;
import com.sunwayworld.framework.audit.aunnotation.Audit;
import com.sunwayworld.framework.context.ApplicationContextHelper;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@GikamBean
@Repository
public class CoreEmailConfigServiceImpl
implements CoreEmailConfigService {
    @Autowired
    private CoreEmailConfigDao configDao;
    @Autowired
    private CoreEmailConfigReceipentService configReceipentService;
    @Autowired
    private CoreEmailConfigRecRoleService configRecRoleService;
    @Autowired
    private CoreEmailConfigCcUserService configCcUserService;
    @Autowired
    private CoreEmailConfigCcRoleService configCcRoleService;

    @Override
    public CoreEmailConfigDao getDao() {
        return this.configDao;
    }

    @Override
    @Transactional
    @AuditTrailEntry(value=AuditTrailType.INSERT)
    @Audit(value="AUDIT.INSERT")
    public String insert(RestJsonWrapperBean wrapper) {
        CoreEmailConfigBean config = wrapper.parseUnique(CoreEmailConfigBean.class);
        this.getDao().insert(config);
        return config.getId();
    }

    @Override
    @Transactional
    @AuditTrailEntry(value=AuditTrailType.INSERT)
    @Audit(value="AUDIT.INSERT")
    public void insertReceiptent(String id, RestJsonWrapperBean wrapper) {
        List<CoreEmailConfigReceipentBean> configReceipentList = wrapper.parse(CoreEmailConfigReceipentBean.class);
        int qty = configReceipentList.size();
        List<Long> idList = ApplicationContextHelper.getNextIdentityList(qty);
        for (int i = 0; i < qty; ++i) {
            CoreEmailConfigReceipentBean configReceipent = configReceipentList.get(i);
            configReceipent.setId(idList.get(i));
            configReceipent.setConfigId(id);
        }
        this.configReceipentService.getDao().insert(configReceipentList);
    }

    @Override
    @Transactional
    @AuditTrailEntry(value=AuditTrailType.DELETE)
    @Audit(value="AUDIT.DELETE")
    public void deleteReceiptent(String id, RestJsonWrapperBean wrapper) {
        List<CoreEmailConfigReceipentBean> configReceipentList = wrapper.parse(CoreEmailConfigReceipentBean.class);
        this.configReceipentService.getDao().deleteBy(configReceipentList, new String[0]);
    }

    @Override
    @Transactional
    @AuditTrailEntry(value=AuditTrailType.INSERT)
    @Audit(value="AUDIT.INSERT")
    public void insertRole(String id, RestJsonWrapperBean wrapper) {
        List<CoreEmailConfigRecRoleBean> configRoleList = wrapper.parse(CoreEmailConfigRecRoleBean.class);
        int qty = configRoleList.size();
        List<Long> idList = ApplicationContextHelper.getNextIdentityList(qty);
        for (int i = 0; i < qty; ++i) {
            CoreEmailConfigRecRoleBean configReceipent = configRoleList.get(i);
            configReceipent.setId(idList.get(i));
            configReceipent.setConfigId(id);
        }
        this.configRecRoleService.getDao().insert(configRoleList);
    }

    @Override
    @Transactional
    @AuditTrailEntry(value=AuditTrailType.DELETE)
    @Audit(value="AUDIT.DELETE")
    public void deleteRole(String id, RestJsonWrapperBean wrapper) {
        List<CoreEmailConfigRecRoleBean> configRoleList = wrapper.parse(CoreEmailConfigRecRoleBean.class);
        this.configRecRoleService.getDao().deleteBy(configRoleList, new String[0]);
    }

    @Override
    @Transactional
    @AuditTrailEntry(value=AuditTrailType.INSERT)
    @Audit(value="AUDIT.INSERT")
    public List<Long> insertCcUser(String id, RestJsonWrapperBean wrapper) {
        List<CoreEmailConfigCcUserBean> configCcUserList = wrapper.parse(CoreEmailConfigCcUserBean.class);
        int qty = configCcUserList.size();
        List<Long> idList = ApplicationContextHelper.getNextIdentityList(qty);
        for (int i = 0; i < qty; ++i) {
            CoreEmailConfigCcUserBean configReceipent = configCcUserList.get(i);
            configReceipent.setId(idList.get(i));
            configReceipent.setConfigId(id);
        }
        this.configCcUserService.getDao().insert(configCcUserList);
        return idList;
    }

    @Override
    @Transactional
    @AuditTrailEntry(value=AuditTrailType.DELETE)
    @Audit(value="AUDIT.DELETE")
    public void deleteCcUser(String id, RestJsonWrapperBean wrapper) {
        List<Long> configCcUserIdList = wrapper.parseId(Long.class);
        this.configCcUserService.getDao().deleteByIdList(configCcUserIdList);
    }

    @Override
    @Transactional
    @AuditTrailEntry(value=AuditTrailType.INSERT)
    @Audit(value="AUDIT.INSERT")
    public List<Long> insertCcRole(String id, RestJsonWrapperBean wrapper) {
        List<CoreEmailConfigCcRoleBean> configCcRoleList = wrapper.parse(CoreEmailConfigCcRoleBean.class);
        int qty = configCcRoleList.size();
        List<Long> idList = ApplicationContextHelper.getNextIdentityList(qty);
        for (int i = 0; i < qty; ++i) {
            CoreEmailConfigCcRoleBean configCcRole = configCcRoleList.get(i);
            configCcRole.setId(idList.get(i));
            configCcRole.setConfigId(id);
        }
        this.configCcRoleService.getDao().insert(configCcRoleList);
        return idList;
    }

    @Override
    @Transactional
    @AuditTrailEntry(value=AuditTrailType.DELETE)
    @Audit(value="AUDIT.DELETE")
    public void deleteCcRole(String id, RestJsonWrapperBean wrapper) {
        List<Long> configCcRoleIdList = wrapper.parseId(Long.class);
        this.configCcRoleService.getDao().deleteByIdList(configCcRoleIdList);
    }
}
