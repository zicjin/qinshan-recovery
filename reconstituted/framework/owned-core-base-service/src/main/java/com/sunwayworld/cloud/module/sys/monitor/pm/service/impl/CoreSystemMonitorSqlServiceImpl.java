/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.sys.monitor.pm.service.impl;

import com.sunwayworld.cloud.module.sys.monitor.pm.bean.CoreSystemMonitorSqlBean;
import com.sunwayworld.cloud.module.sys.monitor.pm.persistent.dao.CoreSystemMonitorSqlDao;
import com.sunwayworld.cloud.module.sys.monitor.pm.service.CoreSystemMonitorSqlService;
import com.sunwayworld.framework.at.annotation.AuditTrailEntry;
import com.sunwayworld.framework.at.annotation.AuditTrailType;
import com.sunwayworld.framework.audit.aunnotation.Audit;
import com.sunwayworld.framework.context.ApplicationContextHelper;
import com.sunwayworld.framework.data.page.Page;
import com.sunwayworld.framework.mybatis.mapper.MapperParameter;
import com.sunwayworld.framework.mybatis.page.PageRowBounds;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@GikamBean
public class CoreSystemMonitorSqlServiceImpl
implements CoreSystemMonitorSqlService {
    @Autowired
    private CoreSystemMonitorSqlDao coreSystemMonitorSqlDao;

    @Override
    public CoreSystemMonitorSqlDao getDao() {
        return this.coreSystemMonitorSqlDao;
    }

    @Override
    @Transactional
    @AuditTrailEntry(value=AuditTrailType.INSERT)
    public Long insert(CoreSystemMonitorSqlBean coreSystemMonitorSqlBean) {
        coreSystemMonitorSqlBean.setId(ApplicationContextHelper.getNextIdentity());
        this.getDao().insert(coreSystemMonitorSqlBean);
        return coreSystemMonitorSqlBean.getId();
    }

    @Override
    @Transactional
    @Audit(value="AUDIT.SAVE")
    public void revise(RestJsonWrapperBean wrapper) {
        List<CoreSystemMonitorSqlBean> sqlList = wrapper.parse(CoreSystemMonitorSqlBean.class);
        sqlList.forEach(e -> e.setRevisionFlag("1"));
        this.getDao().update(sqlList, new String[]{"REVISIONFLAG"});
    }

    @Override
    @Transactional
    @Audit(value="AUDIT.SAVE")
    public void cancelRevise(RestJsonWrapperBean wrapper) {
        List<CoreSystemMonitorSqlBean> sqlList = wrapper.parse(CoreSystemMonitorSqlBean.class);
        sqlList.forEach(e -> e.setRevisionFlag("0"));
        this.getDao().update(sqlList, new String[]{"REVISIONFLAG"});
    }

    @Override
    @Transactional
    @Audit(value="AUDIT.DELETE")
    public void cleanAll() {
        CoreSystemMonitorSqlBean coreSystemMonitorSqlBean = new CoreSystemMonitorSqlBean();
        coreSystemMonitorSqlBean.setRevisionFlag("0");
        this.getDao().deleteBy(coreSystemMonitorSqlBean, "REVISIONFLAG");
    }

    @Override
    public Page<CoreSystemMonitorSqlBean> selectPagination(MapperParameter parameter, PageRowBounds rowBounds) {
        List<Map<String, Object>> sqlList = this.getDao().selectByCondition(parameter);
        for (Map<String, Object> stringObjectMap : sqlList) {
            if (!stringObjectMap.containsKey("SQLSOURCE")) continue;
            stringObjectMap.put("SQLSOURCE", stringObjectMap.get("SQLSOURCE").toString().substring(stringObjectMap.get("SQLSOURCE").toString().indexOf("mapper.") + 7));
        }
        return this.selectPagination(() -> sqlList, rowBounds);
    }
}
