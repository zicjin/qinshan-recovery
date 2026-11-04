/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.module.item.notification.service.impl;

import com.sunwayworld.framework.at.annotation.AuditTrailEntry;
import com.sunwayworld.framework.at.annotation.AuditTrailType;
import com.sunwayworld.framework.audit.aunnotation.Audit;
import com.sunwayworld.framework.context.LocalContextHelper;
import com.sunwayworld.framework.database.sql.Order;
import com.sunwayworld.framework.mybatis.mapper.MatchPattern;
import com.sunwayworld.framework.mybatis.mapper.SearchFilter;
import com.sunwayworld.framework.restful.data.RestJsonWrapperBean;
import com.sunwayworld.framework.spring.annotation.GikamBean;
import com.sunwayworld.module.item.notification.bean.CoreNotificationInstanceToBean;
import com.sunwayworld.module.item.notification.dao.CoreNotificationInstanceToDao;
import com.sunwayworld.module.item.notification.service.CoreNotificationInstanceToService;
import java.time.LocalDateTime;
import java.util.List;
import javax.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@GikamBean
@Repository
public class CoreNotificationInstanceToServiceImpl
implements CoreNotificationInstanceToService {
    @Autowired
    private CoreNotificationInstanceToDao instanceToDao;
    private static final String NOTIFICATION_STATUS_READ = "1";
    private static final String NOTIFICATION_STATUS_IGNORE = "1";

    @Override
    public CoreNotificationInstanceToDao getDao() {
        return this.instanceToDao;
    }

    @Override
    @Transactional
    @AuditTrailEntry(value=AuditTrailType.UPDATE)
    @Audit(value="AUDIT.SAVE")
    public void read(RestJsonWrapperBean wrapper) {
        List<CoreNotificationInstanceToBean> instanceToList = wrapper.parse(CoreNotificationInstanceToBean.class);
        if (instanceToList.isEmpty()) {
            return;
        }
        LocalDateTime now = LocalDateTime.now();
        instanceToList.forEach(i -> {
            i.setRead("1");
            i.setReadTime(now);
        });
        this.getDao().update(instanceToList, new String[]{"READ", "READTIME"});
    }

    @Override
    @Transactional
    @AuditTrailEntry(value=AuditTrailType.UPDATE)
    @Audit(value="AUDIT.SAVE")
    public void ignore(RestJsonWrapperBean wrapper) {
        List<CoreNotificationInstanceToBean> instanceToList = wrapper.parse(CoreNotificationInstanceToBean.class);
        if (instanceToList.isEmpty() && (instanceToList = this.selectListByFilter(SearchFilter.instance().match("NOTIFIEDID", LocalContextHelper.getLoginUserId()).filter(MatchPattern.EQ).match("READ", "0").filter(MatchPattern.EQ).match("IGNORE", "0").filter(MatchPattern.EQ), new Order[0])).isEmpty()) {
            return;
        }
        instanceToList.forEach(i -> i.setIgnore("1"));
        this.getDao().update(instanceToList, new String[]{"IGNORE"});
    }
}
