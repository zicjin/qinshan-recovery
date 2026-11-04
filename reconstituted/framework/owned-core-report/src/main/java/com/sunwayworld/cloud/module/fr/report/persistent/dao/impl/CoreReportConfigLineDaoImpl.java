/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.fr.report.persistent.dao.impl;

import com.sunwayworld.cloud.module.fr.report.bean.CoreReportConfigLineBean;
import com.sunwayworld.cloud.module.fr.report.persistent.dao.CoreReportConfigLineDao;
import com.sunwayworld.cloud.module.fr.report.persistent.mapper.CoreReportConfigLineMapper;
import com.sunwayworld.framework.mybatis.dao.MybatisDaoSupport;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@GikamBean
@Repository
public class CoreReportConfigLineDaoImpl
extends MybatisDaoSupport<CoreReportConfigLineBean, Long>
implements CoreReportConfigLineDao {
    @Autowired
    private CoreReportConfigLineMapper coreReportConfigLineMapper;

    public CoreReportConfigLineMapper getMapper() {
        return this.coreReportConfigLineMapper;
    }

    @Override
    public List<Map<String, Object>> selectTreeList(Map<String, Object> parameter) {
        return this.getMapper().selectTreeList(parameter);
    }
}
