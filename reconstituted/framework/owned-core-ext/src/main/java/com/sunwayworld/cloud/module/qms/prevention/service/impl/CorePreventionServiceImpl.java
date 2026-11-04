/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.qms.prevention.service.impl;

import com.sunwayworld.cloud.module.qms.prevention.bean.CorePreventionBean;
import com.sunwayworld.cloud.module.qms.prevention.bean.CorePreventionItemBean;
import com.sunwayworld.cloud.module.qms.prevention.persistent.dao.CorePreventionDao;
import com.sunwayworld.cloud.module.qms.prevention.service.CorePreventionItemService;
import com.sunwayworld.cloud.module.qms.prevention.service.CorePreventionService;
import com.sunwayworld.framework.at.annotation.AuditTrailEntry;
import com.sunwayworld.framework.at.annotation.AuditTrailType;
import com.sunwayworld.framework.audit.aunnotation.Audit;
import com.sunwayworld.framework.context.ApplicationContextHelper;
import com.sunwayworld.framework.context.LocalContextHelper;
import com.sunwayworld.framework.data.page.Page;
import com.sunwayworld.framework.mybatis.mapper.MatchPattern;
import com.sunwayworld.framework.mybatis.mapper.SearchFilter;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import com.sunwayworld.module.mdm.user.bean.CoreUserBean;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@GikamBean
public class CorePreventionServiceImpl
implements CorePreventionService {
    @Autowired
    private CorePreventionDao corePreventionDao;
    @Autowired
    private CorePreventionItemService corePreventionItemService;

    @Override
    public CorePreventionDao getDao() {
        return this.corePreventionDao;
    }

    @Override
    @Transactional
    @AuditTrailEntry(value=AuditTrailType.INSERT)
    @Audit(value="AUDIT.INSERT")
    public Long insert(RestJsonWrapperBean jsonWrapper) {
        CorePreventionBean corePrevention = jsonWrapper.parseUnique(CorePreventionBean.class);
        corePrevention.setId(ApplicationContextHelper.getNextIdentity());
        corePrevention.setPreventionNo(ApplicationContextHelper.getNextSequence(this.getDao().getTable(), "YF"));
        corePrevention.setDiscoveryOrgId(LocalContextHelper.getLoginOrgId());
        corePrevention.setDiscoveryOrgName(LocalContextHelper.getLoginOrgName());
        this.getDao().insert(corePrevention);
        return corePrevention.getId();
    }

    @Override
    public Page<CorePreventionItemBean> selectItemPaginationByPreventionId(Long id, RestJsonWrapperBean wrapper) {
        return this.corePreventionItemService.selectPaginationByFilter(SearchFilter.instance().match("PREVENTIONID", id).filter(MatchPattern.EQ), wrapper);
    }

    @Override
    @Transactional
    @AuditTrailEntry(value=AuditTrailType.INSERT)
    @Audit(value="AUDIT.INSERT")
    public Long insertItem(Long id, RestJsonWrapperBean wrapper) {
        List<CorePreventionItemBean> corePreventionItemList = wrapper.parse(CorePreventionItemBean.class);
        corePreventionItemList.forEach(e -> {
            e.setId(ApplicationContextHelper.getNextIdentity());
            e.setPreventionId(id);
        });
        this.corePreventionItemService.getDao().insert(corePreventionItemList);
        return corePreventionItemList.get(0).getId();
    }

    @Override
    @Transactional
    @AuditTrailEntry(value=AuditTrailType.DELETE)
    @Audit(value="AUDIT.DELETE")
    public void deleteItem(Long id, RestJsonWrapperBean wrapper) {
        this.corePreventionItemService.delete(wrapper);
    }

    @Override
    @Transactional
    @AuditTrailEntry(value=AuditTrailType.UPDATE)
    @Audit(value="AUDIT.SAVE")
    public void save(Long aLong, RestJsonWrapperBean wrapper) {
        List<CorePreventionBean> corePreventionList = wrapper.parse(CorePreventionBean.class);
        String status = corePreventionList.get(0).getQualityUserName();
        corePreventionList.get(0).setQualityUserName(null);
        CoreUserBean loginUser = LocalContextHelper.getLoginUser();
        if (status.equals("qmAudit")) {
            corePreventionList.stream().forEach(t -> {
                t.setQualityUserId(loginUser.getId());
                t.setQualityUserName(loginUser.getUserName());
            });
        }
        if (status.equals("executeAudit")) {
            corePreventionList.stream().forEach(t -> {
                t.setCarryUserId(loginUser.getId());
                t.setCarryUserName(loginUser.getUserName());
            });
        }
        if (status.equals("groupReview")) {
            corePreventionList.stream().forEach(t -> {
                t.setLeaderUserId(loginUser.getId());
                t.setLeaderUserName(loginUser.getUserName());
            });
        }
        this.getDao().update(corePreventionList, new String[0]);
    }
}
