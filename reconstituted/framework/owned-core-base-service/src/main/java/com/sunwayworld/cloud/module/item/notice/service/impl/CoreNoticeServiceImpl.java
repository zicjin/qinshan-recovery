/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.item.notice.service.impl;

import com.sunwayworld.cloud.module.item.notice.bean.CoreNoticeBean;
import com.sunwayworld.cloud.module.item.notice.bean.CoreNoticePermissionBean;
import com.sunwayworld.cloud.module.item.notice.persistent.dao.CoreNoticeDao;
import com.sunwayworld.cloud.module.item.notice.service.CoreNoticePermissionService;
import com.sunwayworld.cloud.module.item.notice.service.CoreNoticeService;
import com.sunwayworld.framework.at.annotation.AuditTrailEntry;
import com.sunwayworld.framework.at.annotation.AuditTrailType;
import com.sunwayworld.framework.audit.aunnotation.Audit;
import com.sunwayworld.framework.context.ApplicationContextHelper;
import com.sunwayworld.framework.data.page.Page;
import com.sunwayworld.framework.mybatis.mapper.MapperParameter;
import com.sunwayworld.framework.mybatis.mapper.MatchPattern;
import com.sunwayworld.framework.mybatis.mapper.SearchFilter;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import com.sunwayworld.framework.support.PersistableHelper;
import com.sunwayworld.framework.utils.CollectionUtils;
import com.sunwayworld.module.sys.bpmn.engine.CoreBpmnRuntimeSource;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@GikamBean
public class CoreNoticeServiceImpl
implements CoreNoticeService {
    @Autowired
    private CoreNoticeDao noticeDao;
    @Autowired
    @Lazy
    private CoreNoticePermissionService noticePermissionService;

    @Override
    public CoreNoticeDao getDao() {
        return this.noticeDao;
    }

    @Override
    @Transactional
    @AuditTrailEntry(value=AuditTrailType.INSERT)
    @Audit(value="AUDIT.INSERT")
    public Long insert(RestJsonWrapperBean jsonWrapper) {
        CoreNoticeBean notice = jsonWrapper.parseUnique(CoreNoticeBean.class);
        notice.setId(ApplicationContextHelper.getNextIdentity());
        notice.setStatus("normal");
        this.getDao().insert(notice);
        return notice.getId();
    }

    @Override
    @Transactional
    @AuditTrailEntry(value=AuditTrailType.INSERT)
    @Audit(value="AUDIT.INSERT")
    public Long insertPermission(Long id, RestJsonWrapperBean wrapper) {
        List<CoreNoticePermissionBean> noticePermissionList = wrapper.parse(CoreNoticePermissionBean.class);
        noticePermissionList.stream().forEach(e -> {
            e.setId(ApplicationContextHelper.getNextIdentity());
            e.setNoticeId(id);
        });
        this.noticePermissionService.getDao().insert(noticePermissionList);
        return noticePermissionList.get(0).getId();
    }

    @Override
    public Page<CoreNoticePermissionBean> selectPermissionPagationByNotice(Long id, String permissionType, RestJsonWrapperBean wrapper) {
        return this.noticePermissionService.selectPaginationByFilter(SearchFilter.instance().match("NOTICEID", id).filter(MatchPattern.EQ).match("PERMISSIONTYPE", permissionType).filter(MatchPattern.EQ), wrapper);
    }

    @Override
    @Transactional
    @AuditTrailEntry(value=AuditTrailType.DELETE)
    @Audit(value="AUDIT.DELETE")
    public void deletePermission(RestJsonWrapperBean wrapper) {
        this.noticePermissionService.delete(wrapper);
    }

    @Override
    @Transactional
    @AuditTrailEntry(value=AuditTrailType.UPDATE)
    @Audit(value="AUDIT.SAVE")
    public void cancel(RestJsonWrapperBean wrapper) {
        List<CoreNoticeBean> noticeList = wrapper.parse(CoreNoticeBean.class);
        noticeList.forEach(e -> e.setStatus("cancel"));
        this.getDao().update(noticeList, new String[]{"STATUS"});
    }

    @Override
    public List<CoreNoticeBean> selectIndexList(RestJsonWrapperBean wrapper) {
        MapperParameter parameter = wrapper.extractMapFilter();
        List<Map<String, Object>> mapList = this.getDao().selectIndexByCondition(parameter);
        if (mapList.isEmpty()) {
            return CollectionUtils.emptyList();
        }
        return mapList.stream().map(m -> (CoreNoticeBean)PersistableHelper.mapToPersistable(m, this.getDao().getType())).collect(Collectors.toList());
    }

    @Override
    public Supplier<String> getDescSupplier(CoreBpmnRuntimeSource<CoreNoticeBean, Long> source) {
        return () -> ((CoreNoticeBean)source.getOldItem()).getTitle();
    }
}
