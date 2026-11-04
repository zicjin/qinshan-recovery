/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.module.admin.request.service.impl;

import com.sunwayworld.framework.at.annotation.AuditTrailEntry;
import com.sunwayworld.framework.at.annotation.AuditTrailType;
import com.sunwayworld.framework.context.ApplicationContextHelper;
import com.sunwayworld.framework.database.sql.Order;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import com.sunwayworld.framework.support.PersistableHelper;
import com.sunwayworld.module.admin.request.bean.CoreRequestUrlBean;
import com.sunwayworld.module.admin.request.dao.CoreRequestUrlDao;
import com.sunwayworld.module.admin.request.service.CoreRequestUrlService;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@GikamBean
public class CoreRequestUrlServiceImpl
implements CoreRequestUrlService {
    @Autowired
    private CoreRequestUrlDao coreRequestUrlDao;

    @Override
    public CoreRequestUrlDao getDao() {
        return this.coreRequestUrlDao;
    }

    @Override
    @Transactional
    @AuditTrailEntry(value=AuditTrailType.INSERT)
    public Long insert(RestJsonWrapperBean jsonWrapper) {
        CoreRequestUrlBean coreRequestUrl = jsonWrapper.parseUnique(CoreRequestUrlBean.class);
        coreRequestUrl.setId(ApplicationContextHelper.getNextIdentity());
        this.getDao().insert(coreRequestUrl);
        return coreRequestUrl.getId();
    }

    @Override
    @Cacheable(value={"T_CORE_REQUEST_URL.BY_REQUEST_URL"}, key="#requestUrl")
    public List<CoreRequestUrlBean> selectListByRequestUrl(String requestUrl) {
        List<CoreRequestUrlBean> requestUrlList = this.getDao().selectListByOneColumnValue(requestUrl, "URL", new Order[0]);
        requestUrlList.forEach(r -> {
            r.setNote(null);
            PersistableHelper.resetBasicProperties(r);
        });
        return requestUrlList;
    }
}
