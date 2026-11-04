/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.module.sys.sms.schedule;

import com.sunwayworld.framework.context.concurrent.GikamConcurrentLocker;
import com.sunwayworld.framework.database.sql.Order;
import com.sunwayworld.framework.tenant.TenantContext;
import com.sunwayworld.framework.tenant.TenantManager;
import com.sunwayworld.module.sys.sms.bean.CoreSmsBean;
import com.sunwayworld.module.sys.sms.service.CoreSmsService;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@ConditionalOnProperty(name={"sunway.sms.enabled"}, havingValue="true", matchIfMissing=false)
public class CoreSmsSchedule {
    @Autowired
    private CoreSmsService smsService;
    @Autowired
    private TenantManager tenantManager;

    @Scheduled(fixedDelay=60000L)
    public void schedule() {
        this.tenantManager.call("SMS", () -> GikamConcurrentLocker.isolatedRun("SMS#" + TenantContext.getTenant(), () -> {
            CoreSmsBean filter = new CoreSmsBean();
            filter.setStatus("0");
            List smsIdList = this.smsService.getDao().selectIdList((CoreSmsBean)filter, new Order[0]);
            if (smsIdList.isEmpty()) {
                return;
            }
            smsIdList.forEach(i -> this.smsService.send((Long)i));
        }), new String[0]);
    }
}
