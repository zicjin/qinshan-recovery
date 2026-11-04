/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.sys.monitor.errorlog.service.impl;

import com.sunwayworld.cloud.module.sys.monitor.errorlog.bean.CoreSystemMonitorErrorLogBean;
import com.sunwayworld.cloud.module.sys.monitor.errorlog.persistent.dao.CoreSystemMonitorErrorLogDao;
import com.sunwayworld.cloud.module.sys.monitor.errorlog.service.CoreSystemMonitorErrorLogService;
import com.sunwayworld.framework.at.annotation.AuditTrailEntry;
import com.sunwayworld.framework.at.annotation.AuditTrailType;
import com.sunwayworld.framework.context.ApplicationContextHelper;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import com.sunwayworld.framework.utils.StringUtils;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@GikamBean
public class CoreSystemMonitorErrorLogServiceImpl
implements CoreSystemMonitorErrorLogService {
    @Autowired
    private CoreSystemMonitorErrorLogDao coreSystemMonitorErrorLogDao;

    @Override
    public CoreSystemMonitorErrorLogDao getDao() {
        return this.coreSystemMonitorErrorLogDao;
    }

    @Override
    @Transactional
    @AuditTrailEntry(value=AuditTrailType.INSERT)
    public void insert(CoreSystemMonitorErrorLogBean coreSystemMonitorErrorLogBean) {
        coreSystemMonitorErrorLogBean.setId(ApplicationContextHelper.getNextIdentity());
        this.getDao().insert(coreSystemMonitorErrorLogBean);
    }

    @Override
    @Transactional
    @AuditTrailEntry(value=AuditTrailType.UPDATE)
    public void revision(RestJsonWrapperBean wrapper) {
        List<CoreSystemMonitorErrorLogBean> errorLogBeanList = wrapper.parse(CoreSystemMonitorErrorLogBean.class);
        errorLogBeanList.forEach(errorLogBean -> {
            errorLogBean.setRevisionFlag("1");
            errorLogBean.setRemark(!StringUtils.isEmpty(wrapper.getParamValue("remark")) ? wrapper.getParamValue("remark") : null);
        });
        this.getDao().update(errorLogBeanList, new String[]{"REVISIONFLAG", "REMARK"});
    }

    @Override
    @Transactional
    public void clearErrorLog() {
        CoreSystemMonitorErrorLogBean coreSystemMonitorErrorLogBean = new CoreSystemMonitorErrorLogBean();
        coreSystemMonitorErrorLogBean.setRevisionFlag("0");
        this.getDao().deleteBy(coreSystemMonitorErrorLogBean, "REVISIONFLAG");
    }

    @Override
    @Transactional
    public void deleteNoRevisionErrorLog(RestJsonWrapperBean wrapper) {
        List<Long> errorLogIdList = wrapper.parseId(Long.class);
        this.getDao().deleteByIdList(errorLogIdList);
    }

    @Override
    public void cancelRevision(RestJsonWrapperBean wrapper) {
        List<CoreSystemMonitorErrorLogBean> errorLogBeanList = wrapper.parse(CoreSystemMonitorErrorLogBean.class);
        errorLogBeanList.forEach(errorLogBean -> {
            errorLogBean.setRevisionFlag("0");
            errorLogBean.setRemark(null);
        });
        this.getDao().update(errorLogBeanList, new String[]{"REVISIONFLAG", "REMARK"});
    }
}
