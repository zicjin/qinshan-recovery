/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.qms.qualitycontrol.service.impl;

import com.sunwayworld.cloud.module.qms.qualitycontrol.bean.CoreQualityControlBean;
import com.sunwayworld.cloud.module.qms.qualitycontrol.bean.CoreQualityControlItemBean;
import com.sunwayworld.cloud.module.qms.qualitycontrol.persistent.dao.CoreQualityControlDao;
import com.sunwayworld.cloud.module.qms.qualitycontrol.service.CoreQualityControlItemService;
import com.sunwayworld.cloud.module.qms.qualitycontrol.service.CoreQualityControlService;
import com.sunwayworld.framework.at.annotation.AuditTrailEntry;
import com.sunwayworld.framework.at.annotation.AuditTrailType;
import com.sunwayworld.framework.audit.aunnotation.Audit;
import com.sunwayworld.framework.context.ApplicationContextHelper;
import com.sunwayworld.framework.data.page.Page;
import com.sunwayworld.framework.mybatis.mapper.MatchPattern;
import com.sunwayworld.framework.mybatis.mapper.SearchFilter;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@GikamBean
public class CoreQualityControlServiceImpl
implements CoreQualityControlService {
    @Autowired
    private CoreQualityControlDao coreQualityControlDao;
    @Autowired
    private CoreQualityControlItemService coreQualityControlItemService;

    @Override
    public CoreQualityControlDao getDao() {
        return this.coreQualityControlDao;
    }

    @Override
    @Transactional
    @AuditTrailEntry(value=AuditTrailType.INSERT)
    @Audit(value="AUDIT.INSERT")
    public Long insert(RestJsonWrapperBean jsonWrapper) {
        CoreQualityControlBean coreQualityControl = jsonWrapper.parseUnique(CoreQualityControlBean.class);
        coreQualityControl.setId(ApplicationContextHelper.getNextIdentity());
        coreQualityControl.setQcNo(ApplicationContextHelper.getNextSequence(this.getDao().getTable(), "ZLKZ"));
        this.getDao().insert(coreQualityControl);
        return coreQualityControl.getId();
    }

    @Override
    public Page<CoreQualityControlItemBean> selectControlItemPaginationByQualityControlId(Long id, RestJsonWrapperBean wrapper) {
        return this.coreQualityControlItemService.selectPaginationByFilter(SearchFilter.instance().match("AUDITID", id).filter(MatchPattern.EQ), wrapper);
    }

    @Override
    @Transactional
    @AuditTrailEntry(value=AuditTrailType.INSERT)
    @Audit(value="AUDIT.INSERT")
    public Long insertControlItem(Long id, RestJsonWrapperBean wrapper) {
        List<CoreQualityControlItemBean> coreQualityControlItemList = wrapper.parse(CoreQualityControlItemBean.class);
        coreQualityControlItemList.forEach(e -> {
            e.setId(ApplicationContextHelper.getNextIdentity());
            e.setAuditId(id);
        });
        this.coreQualityControlItemService.getDao().insert(coreQualityControlItemList);
        return coreQualityControlItemList.get(0).getId();
    }

    @Override
    @Transactional
    @AuditTrailEntry(value=AuditTrailType.DELETE)
    @Audit(value="AUDIT.DELETE")
    public void deleteControlItem(Long id, RestJsonWrapperBean wrapper) {
        this.coreQualityControlItemService.delete(wrapper);
    }
}
