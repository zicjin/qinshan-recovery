/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.module.sys.log.service.impl;

import com.sunwayworld.framework.context.ApplicationContextHelper;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import com.sunwayworld.framework.support.base.service.GenericExcelBuilderService;
import com.sunwayworld.module.mdm.user.bean.CoreUserBean;
import com.sunwayworld.module.mdm.user.service.CoreUserService;
import com.sunwayworld.module.sys.log.bean.CoreExportLogBean;
import com.sunwayworld.module.sys.log.dao.CoreExportLogDao;
import com.sunwayworld.module.sys.log.service.CoreExportLogService;
import com.sunwayworld.module.sys.menu.bean.CoreMenuBean;
import com.sunwayworld.module.sys.menu.service.CoreMenuService;
import java.time.LocalDateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@GikamBean
public class CoreExportLogServiceImpl
implements CoreExportLogService {
    @Autowired
    private CoreExportLogDao exportLogDao;
    @Autowired
    private CoreUserService userService;
    @Autowired
    private CoreMenuService menuService;

    @Override
    public CoreExportLogDao getDao() {
        return this.exportLogDao;
    }

    @Override
    @Transactional
    public Long insert(CoreExportLogBean coreExportLog) {
        this.getDao().insert(coreExportLog);
        return coreExportLog.getId();
    }

    @Override
    @Transactional
    public void insertByExcelDTO(RestJsonWrapperBean wrapper, GenericExcelBuilderService.DownloadExcelDTO excel, String exportUserId, String requestMode) {
        CoreExportLogBean coreExportLog = new CoreExportLogBean();
        coreExportLog.setId(ApplicationContextHelper.getNextIdentity());
        coreExportLog.setFileName(excel.getFileName());
        coreExportLog.setExportMode(requestMode);
        coreExportLog.setUrl(excel.getUrl());
        coreExportLog.setCreatedById(exportUserId);
        CoreUserBean coreUser = (CoreUserBean)this.userService.selectById(exportUserId);
        coreExportLog.setCreatedByName(coreUser.getUserName());
        coreExportLog.setCreatedByOrgId(coreUser.getOrgId());
        coreExportLog.setCreatedByOrgName(coreUser.getOrgName());
        coreExportLog.setCreatedTime(LocalDateTime.now());
        String menuId = wrapper.getParamValue("menuId");
        CoreMenuBean menu = (CoreMenuBean)this.menuService.selectById(menuId);
        coreExportLog.setMenuName(menu.getMenuName());
        this.getDao().insert(coreExportLog);
    }
}
