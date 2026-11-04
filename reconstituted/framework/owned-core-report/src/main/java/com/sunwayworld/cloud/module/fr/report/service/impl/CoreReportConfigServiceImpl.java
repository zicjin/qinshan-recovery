/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.fr.report.service.impl;

import com.sunwayworld.cloud.module.fr.report.bean.CoreReportConfigBean;
import com.sunwayworld.cloud.module.fr.report.bean.CoreReportConfigLineBean;
import com.sunwayworld.cloud.module.fr.report.persistent.dao.CoreReportConfigDao;
import com.sunwayworld.cloud.module.fr.report.service.CoreReportConfigLineService;
import com.sunwayworld.cloud.module.fr.report.service.CoreReportConfigService;
import com.sunwayworld.framework.at.annotation.AuditTrailEntry;
import com.sunwayworld.framework.at.annotation.AuditTrailType;
import com.sunwayworld.framework.audit.aunnotation.Audit;
import com.sunwayworld.framework.database.sql.Order;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import com.sunwayworld.module.mdm.user.bean.CoreUserPermissionBean;
import com.sunwayworld.module.mdm.user.service.CoreUserPermissionService;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@GikamBean
@Repository
public class CoreReportConfigServiceImpl
implements CoreReportConfigService {
    @Autowired
    private CoreReportConfigDao coreReportConfigDao;
    @Autowired
    private CoreUserPermissionService coreUserPermissionService;
    @Autowired
    @Lazy
    private CoreReportConfigLineService coreReportConfigLineService;

    @Override
    public CoreReportConfigDao getDao() {
        return this.coreReportConfigDao;
    }

    @Override
    @Transactional
    @AuditTrailEntry(value=AuditTrailType.INSERT)
    @Audit(value="AUDIT.INSERT")
    public String insert(RestJsonWrapperBean wrapper) {
        CoreReportConfigBean coreReportConfig = wrapper.parseUnique(CoreReportConfigBean.class);
        this.getDao().insert(coreReportConfig);
        return coreReportConfig.getId();
    }

    @Override
    @Transactional
    @AuditTrailEntry(value=AuditTrailType.DELETE)
    @Audit(value="AUDIT.DELETE")
    public void delete(RestJsonWrapperBean wrapper) {
        List<CoreReportConfigBean> coreReportConfigList = wrapper.parse(CoreReportConfigBean.class);
        List idList = coreReportConfigList.stream().map(r -> r.getId()).collect(Collectors.toList());
        List<CoreReportConfigLineBean> deleteReportConfigLineList = this.coreReportConfigLineService.getDao().selectListByOneColumnValues(idList, "REPORTCONFIGID", new Order[0]);
        ArrayList deletePermissionList = new ArrayList();
        deleteReportConfigLineList.forEach(d -> {
            CoreUserPermissionBean reportPermission = new CoreUserPermissionBean();
            reportPermission.setTargetId(d.getId().toString());
            reportPermission.setPermissionTypeId("T_CORE_REPORT_CONFIG_LINE");
            deletePermissionList.add(reportPermission);
        });
        this.coreUserPermissionService.getDao().deleteBy(deletePermissionList, new String[]{"permissionTypeId", "targetId"});
        CoreReportConfigService.super.delete(wrapper);
    }
}
