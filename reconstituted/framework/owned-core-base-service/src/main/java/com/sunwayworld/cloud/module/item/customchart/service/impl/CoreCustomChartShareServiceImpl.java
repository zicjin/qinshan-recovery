/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.item.customchart.service.impl;

import com.sunwayworld.cloud.module.item.customchart.bean.CoreCustomChartShareBean;
import com.sunwayworld.cloud.module.item.customchart.persistent.dao.CoreCustomChartShareDao;
import com.sunwayworld.cloud.module.item.customchart.service.CoreCustomChartShareService;
import com.sunwayworld.framework.at.annotation.AuditTrailEntry;
import com.sunwayworld.framework.at.annotation.AuditTrailType;
import com.sunwayworld.framework.audit.aunnotation.Audit;
import com.sunwayworld.framework.context.ApplicationContextHelper;
import com.sunwayworld.framework.database.sql.Order;
import com.sunwayworld.framework.mybatis.mapper.MatchPattern;
import com.sunwayworld.framework.mybatis.mapper.SearchFilter;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import java.util.List;
import javax.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Repository;

@Repository
@GikamBean
public class CoreCustomChartShareServiceImpl
implements CoreCustomChartShareService {
    @Autowired
    private CoreCustomChartShareDao coreCustomChartShareDao;
    @Lazy
    @Autowired
    private CoreCustomChartShareService proxyInstance;

    @Override
    public CoreCustomChartShareDao getDao() {
        return this.coreCustomChartShareDao;
    }

    @Override
    @Transactional
    @AuditTrailEntry(value=AuditTrailType.INSERT)
    @Audit(value="AUDIT.INSERT")
    public Long insert(RestJsonWrapperBean jsonWrapper) {
        CoreCustomChartShareBean coreCustomChartShareBean = jsonWrapper.parseUnique(CoreCustomChartShareBean.class);
        List coreCustomChartShareBeanList = this.proxyInstance.selectListByFilter(SearchFilter.instance().match("CHARTID", coreCustomChartShareBean.getChartId()).filter(MatchPattern.SEQ), new Order[0]);
        if (coreCustomChartShareBeanList.size() == 0) {
            coreCustomChartShareBean.setId(ApplicationContextHelper.getNextIdentity());
            this.getDao().insert(coreCustomChartShareBean);
        } else {
            this.getDao().update(coreCustomChartShareBean, new String[0]);
        }
        return coreCustomChartShareBean.getId();
    }

    @Override
    @Transactional
    public List<CoreCustomChartShareBean> selectCoreCustomChartShareBeanByChartId(RestJsonWrapperBean wrapper) {
        CoreCustomChartShareBean coreCustomChartShareBean = wrapper.parseUnique(CoreCustomChartShareBean.class);
        return this.proxyInstance.selectListByFilter(SearchFilter.instance().match("CHARTID", coreCustomChartShareBean.getChartId()).filter(MatchPattern.SEQ), new Order[0]);
    }
}
