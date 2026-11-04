/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.lcdp.log;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.sunwayworld.cloud.module.lcdp.log.ScriptLogInfo;
import com.sunwayworld.framework.context.ApplicationContextHelper;
import com.sunwayworld.framework.context.concurrent.GikamConcurrentLocker;
import com.sunwayworld.framework.i18n.I18nHelper;
import com.sunwayworld.framework.security.authentication.AuthenticationHelper;
import com.sunwayworld.framework.utils.StringUtils;
import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;
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
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Controller;

@Controller
@ServerEndpoint(value="/ws/script-log-websocket/{userToken}")
public class ScriptLogWebSocket {
    private static final Logger log = LoggerFactory.getLogger(ScriptLogWebSocket.class);
    public static final String LCDP_SCRIPT_LOG_TOPIC = "lcdp-log";
    public static ConcurrentHashMap<String, ScriptLogWebSocket> webSocketSet = new ConcurrentHashMap();
    private Session webSocketSession;
    private String userToken;
    private static final ThreadLocal<String> CONTEXT_SCRIPT_DATASOURCE_NAME_HOLDER = new ThreadLocal();
    private static StringRedisTemplate redisTemplate = ApplicationContextHelper.getBean(StringRedisTemplate.class);

    @OnOpen
    public void onOpen(@PathParam(value="userToken") String param, Session webSocketSession, EndpointConfig config) {
        GikamConcurrentLocker.blockRun("LCDP_SCRIPT_LOG_WEBSOCKET", () -> {
            if (webSocketSet.containsKey(param)) {
                Session oldSession = ScriptLogWebSocket.webSocketSet.get((Object)param).webSocketSession;
                if (ScriptLogWebSocket.webSocketSet.get((Object)param).webSocketSession.isOpen()) {
                    try {
                        webSocketSet.get(param).sendMessage(JSONObject.toJSONString(ScriptLogInfo.forceOffline("WARN", I18nHelper.getMessage("LCDP.RUN_LOGS.REPEATED_CONNECTION", new String[0]))));
                        oldSession.close();
                    }
                    catch (IOException e) {
                        log.error(e.getMessage(), e);
                    }
                }
            }
            this.userToken = param;
            this.webSocketSession = webSocketSession;
            webSocketSet.put(param, this);
        });
    }

    @OnClose
    public void onClose() {
        GikamConcurrentLocker.blockRun("LCDP_SCRIPT_LOG_WEBSOCKET", () -> {
            if (!StringUtils.isEmpty(this.userToken)) {
                webSocketSet.remove(this.userToken);
            }
        });
    }

    @OnMessage
    public void onMessage(String message, Session session) {
        if ("ping".equals(message)) {
            this.sendMessage("pong");
        }
    }

    public void pushLog(String type, String log) {
        String userToken = AuthenticationHelper.getLoginUserToken();
        String scriptName = StringUtils.isEmpty(CONTEXT_SCRIPT_DATASOURCE_NAME_HOLDER.get()) ? "" : CONTEXT_SCRIPT_DATASOURCE_NAME_HOLDER.get() + " ";
        ScriptLogInfo logInfo = ScriptLogInfo.of(type, scriptName + log);
        if (!StringUtils.isEmpty(userToken) && webSocketSet.get(userToken) != null) {
            GikamConcurrentLocker.blockRun("LCDP_SCRIPT_LOG_WEBSOCKET_PUSH" + userToken, () -> webSocketSet.get(userToken).sendMessage(JSONObject.toJSONString(logInfo)));
        } else if (!StringUtils.isEmpty(userToken) && webSocketSet.get(userToken) == null) {
            logInfo.setUser(userToken);
            GikamConcurrentLocker.blockRun("LCDP_SCRIPT_LOG_WEBSOCKET_PUSH" + userToken, () -> redisTemplate.convertAndSend(LCDP_SCRIPT_LOG_TOPIC, JSON.toJSONString(logInfo)));
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void sendMessage(String message) {
        try {
            Session session = this.webSocketSession;
            synchronized (session) {
                this.webSocketSession.getBasicRemote().sendText(message);
            }
        }
        catch (IOException e) {
            log.error(e.getMessage());
        }
    }

    public void pushErrorLog(String log) {
        this.pushLog("ERROR", log);
    }

    public void pushInfoLog(String log) {
        this.pushLog("INFO", log);
    }

    @OnError
    public void onError(Session session, Throwable error) throws IOException {
        log.error(error.getMessage(), error);
        if (session.isOpen()) {
            session.close();
        }
    }

    public static void setContextScriptDatasourceName(String name) {
        CONTEXT_SCRIPT_DATASOURCE_NAME_HOLDER.set(name);
    }
}
