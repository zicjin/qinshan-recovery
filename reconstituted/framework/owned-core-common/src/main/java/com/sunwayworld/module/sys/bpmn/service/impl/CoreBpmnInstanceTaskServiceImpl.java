/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.module.sys.bpmn.service.impl;

import com.sunwayworld.framework.at.annotation.AuditTrailEntry;
import com.sunwayworld.framework.at.annotation.AuditTrailType;
import com.sunwayworld.framework.audit.aunnotation.Audit;
import com.sunwayworld.framework.context.ApplicationContextHelper;
import com.sunwayworld.framework.data.page.Page;
import com.sunwayworld.framework.mybatis.mapper.MatchPattern;
import com.sunwayworld.framework.mybatis.mapper.SearchFilter;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import com.sunwayworld.module.sys.bpmn.bean.CoreBpmnInstanceTaskBean;
import com.sunwayworld.module.sys.bpmn.bean.CoreBpmnInstanceTaskRoleBean;
import com.sunwayworld.module.sys.bpmn.bean.CoreBpmnInstanceTaskUserBean;
import com.sunwayworld.module.sys.bpmn.dao.CoreBpmnInstanceTaskDao;
import com.sunwayworld.module.sys.bpmn.service.CoreBpmnInstanceTaskRoleService;
import com.sunwayworld.module.sys.bpmn.service.CoreBpmnInstanceTaskService;
import com.sunwayworld.module.sys.bpmn.service.CoreBpmnInstanceTaskUserService;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.Nullable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@GikamBean
public class CoreBpmnInstanceTaskServiceImpl
implements CoreBpmnInstanceTaskService {
    @Autowired
    private CoreBpmnInstanceTaskDao instanceTaskDao;
    @Autowired
    private CoreBpmnInstanceTaskUserService instanceTaskUserService;
    @Autowired
    private CoreBpmnInstanceTaskRoleService instanceTaskRoleService;

    @Override
    public CoreBpmnInstanceTaskDao getDao() {
        return this.instanceTaskDao;
    }

    @Override
    public List<CoreBpmnInstanceTaskBean> selectAuditableInstanceTaskList(@Nullable String userId, List<Long> instIdList) {
        return this.getDao().selectAuditableInstanceTaskList(userId, instIdList);
    }

    @Override
    @Async
    @Transactional
    @AuditTrailEntry(value=AuditTrailType.DELETE)
    @Audit(value="AUDIT.DELETE")
    public void cleanCandidatorAsync(Long id) {
        CoreBpmnInstanceTaskUserBean taskUserFilter = new CoreBpmnInstanceTaskUserBean();
        taskUserFilter.setInstTaskId(id);
        this.instanceTaskUserService.getDao().deleteBy((CoreBpmnInstanceTaskUserBean)taskUserFilter, "INSTTASKID");
        CoreBpmnInstanceTaskRoleBean taskRoleFilter = new CoreBpmnInstanceTaskRoleBean();
        taskRoleFilter.setInstTaskId(id);
        this.instanceTaskRoleService.getDao().deleteBy((CoreBpmnInstanceTaskRoleBean)taskRoleFilter, "INSTTASKID");
    }

    @Override
    public Page<CoreBpmnInstanceTaskUserBean> selectInstTaskUserList(Long id, RestJsonWrapperBean wrapper) {
        return this.instanceTaskUserService.selectPaginationByFilter(SearchFilter.instance().match("INSTTASKID", id).filter(MatchPattern.EQ), wrapper);
    }

    @Override
    public Page<CoreBpmnInstanceTaskRoleBean> selectInstTaskRoleList(Long id, RestJsonWrapperBean wrapper) {
        return this.instanceTaskRoleService.selectPaginationByFilter(SearchFilter.instance().match("INSTTASKID", id).filter(MatchPattern.EQ), wrapper);
    }

    @Override
    @Transactional
    @AuditTrailEntry(value=AuditTrailType.INSERT)
    @Audit(value="AUDIT.INSERT")
    public void insertInstTaskUser(Long id, RestJsonWrapperBean wrapper) {
        List<CoreBpmnInstanceTaskUserBean> instanceTaskUserList = wrapper.parse(CoreBpmnInstanceTaskUserBean.class);
        CoreBpmnInstanceTaskBean instanceTask = (CoreBpmnInstanceTaskBean)this.getDao().selectById(id);
        instanceTaskUserList.forEach(t -> {
            t.setId(ApplicationContextHelper.getNextIdentity());
            t.setInstId(instanceTask.getInstId());
            t.setInstTaskId(id);
        });
        this.instanceTaskUserService.getDao().insert(instanceTaskUserList);
    }

    @Override
    @Transactional
    @AuditTrailEntry(value=AuditTrailType.INSERT)
    @Audit(value="AUDIT.INSERT")
    public void insertInstTaskRole(Long id, RestJsonWrapperBean wrapper) {
        List<CoreBpmnInstanceTaskRoleBean> instanceTaskRoleList = wrapper.parse(CoreBpmnInstanceTaskRoleBean.class);
        CoreBpmnInstanceTaskBean instanceTask = (CoreBpmnInstanceTaskBean)this.getDao().selectById(id);
        instanceTaskRoleList.forEach(t -> {
            t.setId(ApplicationContextHelper.getNextIdentity());
            t.setInstId(instanceTask.getInstId());
            t.setInstTaskId(id);
        });
        this.instanceTaskRoleService.getDao().insert(instanceTaskRoleList);
    }

    @Override
    @Transactional
    @AuditTrailEntry(value=AuditTrailType.DELETE)
    @Audit(value="AUDIT.DELETE")
    public void deleteInstTaskUser(Long id, RestJsonWrapperBean wrapper) {
        List<CoreBpmnInstanceTaskUserBean> instanceTaskUserList = wrapper.parse(CoreBpmnInstanceTaskUserBean.class);
        this.instanceTaskUserService.getDao().deleteBy(instanceTaskUserList, new String[0]);
    }

    @Override
    @Transactional
    @AuditTrailEntry(value=AuditTrailType.DELETE)
    @Audit(value="AUDIT.DELETE")
    public void deleteInstTaskRole(Long id, RestJsonWrapperBean wrapper) {
        List<CoreBpmnInstanceTaskRoleBean> instanceTaskRoleList = wrapper.parse(CoreBpmnInstanceTaskRoleBean.class);
        this.instanceTaskRoleService.getDao().deleteBy(instanceTaskRoleList, new String[0]);
    }
}
