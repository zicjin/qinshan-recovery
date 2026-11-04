/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.qms.launchnewproject.service.impl;

import com.sunwayworld.cloud.module.qms.launchnewproject.bean.CoreLaunchNewProjectBean;
import com.sunwayworld.cloud.module.qms.launchnewproject.bean.CoreLaunchNewProjectItemBean;
import com.sunwayworld.cloud.module.qms.launchnewproject.persistent.dao.CoreLaunchNewProjectDao;
import com.sunwayworld.cloud.module.qms.launchnewproject.service.CoreLaunchNewProjectItemService;
import com.sunwayworld.cloud.module.qms.launchnewproject.service.CoreLaunchNewProjectService;
import com.sunwayworld.framework.at.annotation.AuditTrailEntry;
import com.sunwayworld.framework.at.annotation.AuditTrailType;
import com.sunwayworld.framework.audit.aunnotation.Audit;
import com.sunwayworld.framework.context.ApplicationContextHelper;
import com.sunwayworld.framework.data.page.Page;
import com.sunwayworld.framework.mybatis.mapper.MatchPattern;
import com.sunwayworld.framework.mybatis.mapper.SearchFilter;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@GikamBean
public class CoreLaunchNewProjectServiceImpl
implements CoreLaunchNewProjectService {
    @Autowired
    private CoreLaunchNewProjectDao coreLaunchNewProjectDao;
    @Autowired
    private CoreLaunchNewProjectItemService coreLaunchNewProjectItemService;

    @Override
    public CoreLaunchNewProjectDao getDao() {
        return this.coreLaunchNewProjectDao;
    }

    @Override
    @Transactional
    @AuditTrailEntry(value=AuditTrailType.INSERT)
    @Audit(value="AUDIT.INSERT")
    public Long insert(RestJsonWrapperBean jsonWrapper) {
        CoreLaunchNewProjectBean coreLaunchNewProject = jsonWrapper.parseUnique(CoreLaunchNewProjectBean.class);
        coreLaunchNewProject.setId(ApplicationContextHelper.getNextIdentity());
        coreLaunchNewProject.setProjectNo(ApplicationContextHelper.getNextSequence(this.getDao().getTable(), "XXM"));
        this.getDao().insert(coreLaunchNewProject);
        return coreLaunchNewProject.getId();
    }

    @Override
    public Page<CoreLaunchNewProjectItemBean> selectNewProjectItemPaginationByLaunchNewProjectId(Long id, RestJsonWrapperBean wrapper) {
        return this.coreLaunchNewProjectItemService.selectPaginationByFilter(SearchFilter.instance().match("NEWPROJECTID", id).filter(MatchPattern.EQ), wrapper);
    }

    @Override
    @Transactional
    @AuditTrailEntry(value=AuditTrailType.INSERT)
    @Audit(value="AUDIT.INSERT")
    public List<Long> insertNewProjectItem(Long id, RestJsonWrapperBean wrapper) {
        List<CoreLaunchNewProjectItemBean> coreLaunchNewProjectItemList = wrapper.parse(CoreLaunchNewProjectItemBean.class);
        ArrayList<Long> itemIdList = new ArrayList<Long>();
        coreLaunchNewProjectItemList.forEach(e -> {
            Long itemId = ApplicationContextHelper.getNextIdentity();
            e.setId(itemId);
            itemIdList.add(itemId);
            e.setNewProjectId(id);
        });
        this.coreLaunchNewProjectItemService.getDao().insert(coreLaunchNewProjectItemList);
        return itemIdList;
    }

    @Override
    @Transactional
    @AuditTrailEntry(value=AuditTrailType.DELETE)
    @Audit(value="AUDIT.DELETE")
    public void deleteNewProjectItem(Long id, RestJsonWrapperBean wrapper) {
        this.coreLaunchNewProjectItemService.delete(wrapper);
    }
}
