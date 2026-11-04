/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.module.sys.remotecall.schedule.impl;

import com.sunwayworld.framework.context.concurrent.GikamConcurrentLocker;
import com.sunwayworld.framework.database.sql.Order;
import com.sunwayworld.framework.tenant.TenantContext;
import com.sunwayworld.framework.tenant.TenantManager;
import com.sunwayworld.module.sys.remotecall.bean.CoreRemoteCallConfigBean;
import com.sunwayworld.module.sys.remotecall.bean.CoreRemoteCallRequestBean;
import com.sunwayworld.module.sys.remotecall.schedule.CoreRemoteCallScheduleService;
import com.sunwayworld.module.sys.remotecall.service.CoreRemoteCallConfigService;
import com.sunwayworld.module.sys.remotecall.service.CoreRemoteCallRequestService;
import java.util.List;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Repository;

@Repository
@Profile(value={"schedule"})
public class CoreRemoteCallScheduleServiceImpl
implements CoreRemoteCallScheduleService {
    private static final Logger logger = LogManager.getLogger(CoreRemoteCallScheduleService.class);
    private static final String CACHE_KEY = "CORE_REMOTE_CALL";
    @Autowired
    private CoreRemoteCallConfigService remoteCallConfigService;
    @Autowired
    private CoreRemoteCallRequestService remoteCallRequestService;
    @Autowired
    private TenantManager tenantManager;
    @Value(value="${sunway.remote-call.estimated-execution-time:300}")
    private int estimatedExecutionTime;

    @Override
    @Scheduled(fixedDelay=10000L)
    public void schedule() {
        this.tenantManager.call("REMOTE_CALL", () -> {
            String cacheKey = "CORE_REMOTE_CALL#" + TenantContext.getTenant();
            GikamConcurrentLocker.isolatedRun(cacheKey, () -> {
                CoreRemoteCallRequestBean requestFilter = new CoreRemoteCallRequestBean();
                requestFilter.setStatus("0");
                List<CoreRemoteCallRequestBean> requestList = this.remoteCallRequestService.selectList(requestFilter, Order.asc("ID"));
                if (!requestList.isEmpty()) {
                    CoreRemoteCallConfigBean configFilter = new CoreRemoteCallConfigBean();
                    configFilter.setSynch("0");
                    configFilter.setLastSuspendedFlag("0");
                    List<CoreRemoteCallConfigBean> configList = this.remoteCallConfigService.selectList(configFilter, Order.asc("ID"));
                    for (CoreRemoteCallRequestBean request : requestList) {
                        if (!configList.stream().anyMatch(c -> request.getConfigId().equals(c.getId()))) continue;
                        try {
                            GikamConcurrentLocker.isolatedRun(cacheKey + "::ASYNC_REQ::" + request.getId(), () -> this.remoteCallRequestService.processAsyncRequest(request));
                        }
                        catch (Exception ex) {
                            logger.error(ex.getMessage(), (Throwable)ex);
                        }
                    }
                }
            });
        }, new String[0]);
    }
}
