/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.framework.security.schedule;

import com.sunwayworld.framework.context.concurrent.GikamConcurrentLocker;
import com.sunwayworld.framework.database.sql.Order;
import com.sunwayworld.framework.i18n.I18nHelper;
import com.sunwayworld.framework.security.authentication.AuthenticationHelper;
import com.sunwayworld.framework.tenant.TenantContext;
import com.sunwayworld.framework.tenant.TenantManager;
import com.sunwayworld.framework.utils.BeanUtils;
import com.sunwayworld.module.sys.log.bean.CoreLoginLogBean;
import com.sunwayworld.module.sys.log.service.CoreLoginLogService;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Repository;

@Repository
public class AuthenticationExpiredSchedule {
    @Autowired
    private CoreLoginLogService loginLogService;
    @Autowired
    private TenantManager tenantManager;

    @Scheduled(fixedDelay=30000L)
    public void schedule() {
        this.tenantManager.call("AUTHENTICATION_EXPIRED_SCHEDULE", () -> GikamConcurrentLocker.isolatedRun("SECDURITY_ONLINE_TESTING_SCHEDULE#" + TenantContext.getTenant(), () -> {
            CoreLoginLogBean filter = BeanUtils.getPropertyListenerProxy(CoreLoginLogBean.class);
            filter.setStatus("1");
            filter.setLogoutTime(null);
            List<CoreLoginLogBean> loginLogList = this.loginLogService.selectList(filter, new Order[0]);
            if (!loginLogList.isEmpty()) {
                for (CoreLoginLogBean loginLog : loginLogList) {
                    Authentication authentication = AuthenticationHelper.getCacheAuthentication(loginLog.getToken());
                    if (authentication != null) continue;
                    AuthenticationHelper.logout(loginLog.getUserId(), loginLog.getToken(), I18nHelper.getMessage("GIKAM.SECURITY.LOGIN.TIMEOUT", new String[0]));
                }
            }
        }), new String[0]);
    }
}
