/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.module.admin.config.service.impl;

import com.sunwayworld.framework.at.annotation.AuditTrailEntry;
import com.sunwayworld.framework.at.annotation.AuditTrailType;
import com.sunwayworld.framework.audit.aunnotation.Audit;
import com.sunwayworld.framework.context.ApplicationContextHelper;
import com.sunwayworld.framework.i18n.I18nHelper;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import com.sunwayworld.framework.utils.StringUtils;
import com.sunwayworld.module.admin.config.bean.CoreAdminChooseConfigBean;
import com.sunwayworld.module.admin.config.dao.CoreAdminChooseConfigDao;
import com.sunwayworld.module.admin.config.service.CoreAdminChooseConfigService;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.TreeSet;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@GikamBean
public class CoreAdminChooseConfigServiceImpl
implements CoreAdminChooseConfigService {
    @Autowired
    private CoreAdminChooseConfigDao dao;
    @Lazy
    @Autowired
    private CoreAdminChooseConfigService proxyInstance;

    @Override
    public CoreAdminChooseConfigDao getDao() {
        return this.dao;
    }

    @Override
    @Cacheable(value={"T_CORE_ADMIN_CHOOSE_CONFIG"}, key="'ALL'")
    public List<CoreAdminChooseConfigBean> selectAll() {
        List<CoreAdminChooseConfigBean> coreAdminChooseConfigBeans = CoreAdminChooseConfigService.super.selectAll();
        coreAdminChooseConfigBeans.forEach(coreAdminChooseConfigBean -> {
            if (StringUtils.isBlank(coreAdminChooseConfigBean.getI18nCode())) {
                return;
            }
            coreAdminChooseConfigBean.setTitle(I18nHelper.getMessage(coreAdminChooseConfigBean.getI18nCode(), new String[0]));
        });
        return coreAdminChooseConfigBeans.stream().collect(Collectors.collectingAndThen(Collectors.toCollection(() -> new TreeSet<CoreAdminChooseConfigBean>(Comparator.comparing(CoreAdminChooseConfigBean::getCategory))), ArrayList::new));
    }

    @Override
    public List<CoreAdminChooseConfigBean> selectSelectableList() {
        return this.proxyInstance.selectAll();
    }

    @Override
    @Transactional
    @AuditTrailEntry(value=AuditTrailType.INSERT)
    @Audit(value="AUDIT.INSERT")
    public Long insert(RestJsonWrapperBean wrapper) {
        CoreAdminChooseConfigBean config = wrapper.parseUnique(CoreAdminChooseConfigBean.class);
        config.setId(ApplicationContextHelper.getNextIdentity());
        this.getDao().insert(config);
        return config.getId();
    }
}
