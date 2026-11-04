/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.qms.rectify.service.impl;

import com.sunwayworld.cloud.module.qms.rectify.bean.CoreRectifyBean;
import com.sunwayworld.cloud.module.qms.rectify.bean.CoreRectifyItemBean;
import com.sunwayworld.cloud.module.qms.rectify.persistent.dao.CoreRectifyDao;
import com.sunwayworld.cloud.module.qms.rectify.service.CoreRectifyItemService;
import com.sunwayworld.cloud.module.qms.rectify.service.CoreRectifyService;
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
public class CoreRectifyServiceImpl
implements CoreRectifyService {
    @Autowired
    private CoreRectifyDao coreRectifyDao;
    @Autowired
    private CoreRectifyItemService coreRectifyItemService;

    @Override
    public CoreRectifyDao getDao() {
        return this.coreRectifyDao;
    }

    @Override
    @Transactional
    @AuditTrailEntry(value=AuditTrailType.INSERT)
    @Audit(value="AUDIT.INSERT")
    public Long insert(RestJsonWrapperBean jsonWrapper) {
        CoreRectifyBean coreRectify = jsonWrapper.parseUnique(CoreRectifyBean.class);
        coreRectify.setId(ApplicationContextHelper.getNextIdentity());
        coreRectify.setRectifyNo(ApplicationContextHelper.getNextSequence(this.getDao().getTable(), "JZ"));
        coreRectify.setDiscoveryOrgId(LocalContextHelper.getLoginOrgId());
        coreRectify.setDiscoveryOrgName(LocalContextHelper.getLoginOrgName());
        this.getDao().insert(coreRectify);
        return coreRectify.getId();
    }

    @Override
    public Page<CoreRectifyItemBean> selectItemPaginationByRectifyId(Long id, RestJsonWrapperBean wrapper) {
        return this.coreRectifyItemService.selectPaginationByFilter(SearchFilter.instance().match("RECTIFYID", id).filter(MatchPattern.EQ), wrapper);
    }

    @Override
    @Transactional
    @AuditTrailEntry(value=AuditTrailType.INSERT)
    @Audit(value="AUDIT.INSERT")
    public Long insertItem(Long id, RestJsonWrapperBean wrapper) {
        List<CoreRectifyItemBean> coreRectifyItemList = wrapper.parse(CoreRectifyItemBean.class);
        coreRectifyItemList.forEach(e -> {
            e.setId(ApplicationContextHelper.getNextIdentity());
            e.setRectifyId(id);
        });
        this.coreRectifyItemService.getDao().insert(coreRectifyItemList);
        return coreRectifyItemList.get(0).getId();
    }

    @Override
    @Transactional
    @AuditTrailEntry(value=AuditTrailType.DELETE)
    @Audit(value="AUDIT.DELETE")
    public void deleteItem(Long id, RestJsonWrapperBean wrapper) {
        this.coreRectifyItemService.delete(wrapper);
    }

    @Override
    @Transactional
    @AuditTrailEntry(value=AuditTrailType.UPDATE)
    @Audit(value="AUDIT.SAVE")
    public void save(Long id, RestJsonWrapperBean wrapper) {
        List<CoreRectifyBean> coreRectifyList = wrapper.parse(CoreRectifyBean.class);
        String status = coreRectifyList.get(0).getQualityUserName();
        coreRectifyList.get(0).setQualityUserName(null);
        CoreUserBean loginUser = LocalContextHelper.getLoginUser();
        if (status.equals("qmAudit")) {
            coreRectifyList.stream().forEach(t -> {
                t.setQualityUserId(loginUser.getId());
                t.setQualityUserName(loginUser.getUserName());
            });
        }
        if (status.equals("executeAudit")) {
            coreRectifyList.stream().forEach(t -> {
                t.setCarryUserId(loginUser.getId());
                t.setCarryUserName(loginUser.getUserName());
            });
        }
        if (status.equals("groupReview")) {
            coreRectifyList.stream().forEach(t -> {
                t.setLeaderUserId(loginUser.getId());
                t.setLeaderUserName(loginUser.getUserName());
            });
        }
        this.getDao().update(coreRectifyList, new String[0]);
    }
}
