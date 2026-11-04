/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.im.support;

import com.alibaba.fastjson.JSONObject;
import com.microsoft.sqlserver.jdbc.StringUtils;
import com.sunwayworld.cloud.module.im.bean.CoreImMessageDTO;
import com.sunwayworld.cloud.module.im.support.ImHelper;
import com.sunwayworld.framework.cache.redis.RedisHelper;
import com.sunwayworld.framework.context.concurrent.GikamConcurrentLocker;
import com.sunwayworld.framework.utils.JsonUtils;
import java.io.IOException;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import javax.websocket.EndpointConfig;
import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;

@Controller
@ServerEndpoint(value="/websocket/{userId}")
public class ImWebSocket {
    private static final Logger log = LoggerFactory.getLogger(ImWebSocket.class);
    private static ConcurrentHashMap<String, ImWebSocket> webSocketSet = new ConcurrentHashMap();
    private Session webSocketSession;
    private String userId;

    @OnOpen
    public void onOpen(@PathParam(value="userId") String param, Session webSocketSession, EndpointConfig config) {
        GikamConcurrentLocker.blockRun("IM_SERVICE_NAME::IM_SERVICE_KEY", () -> {
            webSocketSet.put(param, this);
            this.userId = param;
            this.webSocketSession = webSocketSession;
            webSocketSet.put(param, this);
            Set<String> onlineUserIdSet = ImHelper.getOnlineUser();
            if (!onlineUserIdSet.contains(this.userId)) {
                List<String> onlineUserIdList = onlineUserIdSet.stream().collect(Collectors.toList());
                onlineUserIdSet.add(this.userId);
                RedisHelper.put("IM_ONLINE_USER_NAME", "IM_ONLINE_USER_KEY", onlineUserIdSet);
                if (!onlineUserIdList.isEmpty()) {
                    List<CoreImMessageDTO> messageDTOList = ImHelper.buildOnlineOfflineMessage(onlineUserIdList, this.userId, "online");
                    messageDTOList.forEach(e -> this.sendToUser((CoreImMessageDTO)e));
                }
            }
        });
    }

    @OnClose
    public void onClose() {
        GikamConcurrentLocker.blockRun("IM_SERVICE_NAME::IM_SERVICE_KEY", () -> {
            if (!StringUtils.isEmpty(this.userId)) {
                webSocketSet.remove(this.userId);
                Set<String> onlineUserIdSet = ImHelper.getOnlineUser();
                if (onlineUserIdSet.contains(this.userId)) {
                    onlineUserIdSet.remove(this.userId);
                    RedisHelper.put("IM_ONLINE_USER_NAME", "IM_ONLINE_USER_KEY", onlineUserIdSet);
                    List<String> onlineUserIdList = onlineUserIdSet.stream().collect(Collectors.toList());
                    if (!onlineUserIdList.isEmpty()) {
                        List<CoreImMessageDTO> messageDTOList = ImHelper.buildOnlineOfflineMessage(onlineUserIdList, this.userId, "offline");
                        messageDTOList.forEach(e -> this.sendToUser((CoreImMessageDTO)e));
                    }
                }
            }
        });
    }

    @OnMessage
    public void onMessage(String message, Session session) {
        CoreImMessageDTO imMessage = JsonUtils.parse(message, CoreImMessageDTO.class);
        this.sendToUser(imMessage);
    }

    public void sendToUser(CoreImMessageDTO message) {
        String receiveUserId = message.getReceiveUserId();
        if (webSocketSet.get(receiveUserId) != null) {
            webSocketSet.get(receiveUserId).sendMessage(JSONObject.toJSONString(message));
        }
    }

    @OnError
    public void onError(Session session, Throwable error) {
        log.error(error.getMessage());
    }

    public void sendMessage(String message) {
        try {
            this.webSocketSession.getBasicRemote().sendText(message);
        }
        catch (IOException e) {
            log.error(e.getMessage());
        }
    }
}
