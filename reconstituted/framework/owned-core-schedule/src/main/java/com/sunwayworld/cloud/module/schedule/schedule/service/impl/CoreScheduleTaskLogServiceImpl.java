/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.schedule.schedule.service.impl;

import com.sunwayworld.cloud.module.schedule.bean.CoreScheduleTaskLogBean;
import com.sunwayworld.cloud.module.schedule.schedule.persistent.dao.CoreScheduleTaskLogDao;
import com.sunwayworld.cloud.module.schedule.schedule.service.CoreScheduleTaskLogService;
import com.sunwayworld.framework.data.page.Page;
import com.sunwayworld.framework.mybatis.mapper.MapperParameter;
import com.sunwayworld.framework.mybatis.mapper.MatchPattern;
import com.sunwayworld.framework.mybatis.mapper.SearchFilter;
import com.sunwayworld.framework.mybatis.page.PageRowBounds;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@GikamBean
@Repository
public class CoreScheduleTaskLogServiceImpl
implements CoreScheduleTaskLogService {
    @Autowired
    private CoreScheduleTaskLogDao taskLogDao;

    @Override
    public CoreScheduleTaskLogDao getDao() {
        return this.taskLogDao;
    }

    @Override
    public Page<CoreScheduleTaskLogBean> selectPagination(String taskId, RestJsonWrapperBean wrapper) {
        MapperParameter parameter = wrapper.extractMapFilter();
        parameter.setFilter(SearchFilter.instance().match("TASKID", taskId).filter(MatchPattern.EQ));
        PageRowBounds rowBounds = wrapper.extractPageRowBounds();
        return this.selectPagination(parameter, rowBounds);
    }

    @Override
    public void update(List<CoreScheduleTaskLogBean> coreScheduleTaskLogList, String ... updateColNames) {
        this.getDao().update(coreScheduleTaskLogList, updateColNames);
    }
}
