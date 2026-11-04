/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.qms.filetype.service.impl;

import com.sunwayworld.cloud.module.qms.filetype.bean.CoreFileCategoryRoleAuthBean;
import com.sunwayworld.cloud.module.qms.filetype.persistent.dao.CoreFileCategoryRoleAuthDao;
import com.sunwayworld.cloud.module.qms.filetype.service.CoreFileCategoryRoleAuthService;
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

@Repository
@GikamBean
public class CoreFileCategoryRoleAuthServiceImpl
implements CoreFileCategoryRoleAuthService {
    @Autowired
    private CoreFileCategoryRoleAuthDao coreFileCategoryRoleAuthDao;

    @Override
    public CoreFileCategoryRoleAuthDao getDao() {
        return this.coreFileCategoryRoleAuthDao;
    }

    @Override
    @Transactional
    @AuditTrailEntry(value=AuditTrailType.INSERT)
    @Audit(value="AUDIT.INSERT")
    public Long insert(RestJsonWrapperBean jsonWrapper) {
        List<CoreFileCategoryRoleAuthBean> coreFileCategoryRoleAuthList = jsonWrapper.parse(CoreFileCategoryRoleAuthBean.class);
        List<Long> idList = ApplicationContextHelper.getNextIdentityList(coreFileCategoryRoleAuthList.size());
        for (int i = 0; i < coreFileCategoryRoleAuthList.size(); ++i) {
            coreFileCategoryRoleAuthList.get(i).setId(idList.get(i));
        }
        this.getDao().insert(coreFileCategoryRoleAuthList);
        return idList.get(0);
    }
}
