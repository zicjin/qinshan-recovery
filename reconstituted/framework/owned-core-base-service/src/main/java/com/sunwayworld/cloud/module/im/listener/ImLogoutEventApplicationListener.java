/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.im.listener;

import com.sunwayworld.cloud.module.im.bean.CoreImMessageDTO;
import com.sunwayworld.cloud.module.im.support.ImHelper;
import com.sunwayworld.cloud.module.im.support.ImWebSocket;
import com.sunwayworld.framework.cache.redis.RedisHelper;
import com.sunwayworld.framework.context.concurrent.GikamConcurrentLocker;
import com.sunwayworld.framework.security.bean.CoreLogoutEvent;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

@Component
public class ImLogoutEventApplicationListener
implements ApplicationListener<CoreLogoutEvent> {
    @Autowired
    private ImWebSocket imWebSocket;

    @Override
    public void onApplicationEvent(CoreLogoutEvent logoutEvent) {
        GikamConcurrentLocker.blockRun("IM_SERVICE_NAME::IM_SERVICE_KEY", () -> {
            String userId = logoutEvent.getUserId();
            Set<String> onlineUserIdSet = ImHelper.getOnlineUser();
            if (onlineUserIdSet.contains(userId)) {
                onlineUserIdSet.remove(userId);
                RedisHelper.put("IM_ONLINE_USER_NAME", "IM_ONLINE_USER_KEY", onlineUserIdSet);
                List<String> onlineUserIdList = onlineUserIdSet.stream().collect(Collectors.toList());
                if (!onlineUserIdList.isEmpty()) {
                    List<CoreImMessageDTO> messageDTOList = ImHelper.buildOnlineOfflineMessage(onlineUserIdList, userId, "offline");
                    messageDTOList.forEach(e -> this.imWebSocket.sendToUser((CoreImMessageDTO)e));
                }
            }
        });
    }
}
