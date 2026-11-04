/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.module.item.file.service.impl;

import com.sunwayworld.framework.at.annotation.AuditTrailEntry;
import com.sunwayworld.framework.at.annotation.AuditTrailType;
import com.sunwayworld.framework.context.ApplicationContextHelper;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import com.sunwayworld.module.item.file.bean.CoreFileLogBean;
import com.sunwayworld.module.item.file.dao.CoreFileLogDao;
import com.sunwayworld.module.item.file.service.CoreFileLogService;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@GikamBean
public class CoreFileLogServiceImpl
implements CoreFileLogService {
    @Autowired
    private CoreFileLogDao coreFileLogDao;

    @Override
    public CoreFileLogDao getDao() {
        return this.coreFileLogDao;
    }

    @Override
    @Transactional
    @AuditTrailEntry(value=AuditTrailType.INSERT)
    public Long insert(RestJsonWrapperBean jsonWrapper) {
        CoreFileLogBean coreFileLog = jsonWrapper.parseUnique(CoreFileLogBean.class);
        coreFileLog.setId(ApplicationContextHelper.getNextIdentity());
        this.getDao().insert(coreFileLog);
        return coreFileLog.getId();
    }

    @Override
    @Transactional
    @AuditTrailEntry(value=AuditTrailType.INSERT)
    public Long insert(Long fileId, String fileOperationCategory) {
        CoreFileLogBean fileLog = new CoreFileLogBean();
        fileLog.setId(ApplicationContextHelper.getNextIdentity());
        fileLog.setFileId(fileId);
        fileLog.setOperationCategory(fileOperationCategory);
        this.getDao().insert(fileLog);
        return fileLog.getId();
    }

    @Override
    @Transactional
    @AuditTrailEntry(value=AuditTrailType.INSERT)
    public Long insert(List<Long> fileIdList, String fileOperationCategory) {
        if (fileIdList.isEmpty()) {
            return null;
        }
        List fileLogList = fileIdList.stream().map(id -> {
            CoreFileLogBean fileLog = new CoreFileLogBean();
            fileLog.setId(ApplicationContextHelper.getNextIdentity());
            fileLog.setFileId((Long)id);
            fileLog.setOperationCategory(fileOperationCategory);
            return fileLog;
        }).collect(Collectors.toList());
        this.getDao().insert(fileLogList);
        return ((CoreFileLogBean)fileLogList.get(0)).getId();
    }
}
