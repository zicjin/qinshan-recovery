/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.framework.security.token;

import com.sunwayworld.framework.cache.redis.RedisHelper;
import com.sunwayworld.framework.security.token.ResetTokenDTO;
import com.sunwayworld.framework.security.token.ResetTokenQueue;
import com.sunwayworld.framework.tenant.TenantManager;
import java.util.ArrayList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class ResetTokenConsumer {
    private static final int MIN_LOGIN_TIMEOUT = 10;
    private static final int MIN_MOBILE_LOGIN_TIMEOUT = 300;
    private static final int MAX_RESET_SIZE = 50;
    @Autowired
    private TenantManager tenantManager;
    @Value(value="${sunway.security.login-timeout:30}")
    private int loginTimeout;
    @Value(value="${sunway.security.mobile-login-timeout:300}")
    private int mobileLoginTimeout;

    @Scheduled(fixedDelay=5000L)
    public void consume() {
        ResetTokenQueue queue = ResetTokenQueue.INSTANCE;
        if (this.loginTimeout < 10) {
            this.loginTimeout = 10;
        }
        if (this.mobileLoginTimeout < 300) {
            this.mobileLoginTimeout = 300;
        }
        ArrayList<ResetTokenDTO> resetTokenList = new ArrayList<ResetTokenDTO>();
        while (!queue.isEmpty()) {
            ResetTokenDTO resetToken = queue.poll();
            if (!resetTokenList.contains(resetToken)) {
                resetTokenList.add(resetToken);
            }
            if (resetTokenList.size() < 50) continue;
            resetTokenList.forEach(t -> this.tenantManager.tenantCall("RESET_TOKEN", t.getTenant(), () -> RedisHelper.expire("security#rememberme", t.getToken(), t.isMobile() ? this.mobileLoginTimeout : this.loginTimeout)));
            resetTokenList.clear();
        }
        if (!resetTokenList.isEmpty()) {
            resetTokenList.forEach(t -> this.tenantManager.tenantCall("RESET_TOKEN", t.getTenant(), () -> RedisHelper.expire("security#rememberme", t.getToken(), t.isMobile() ? this.mobileLoginTimeout : this.loginTimeout)));
            resetTokenList.clear();
        }
    }
}
