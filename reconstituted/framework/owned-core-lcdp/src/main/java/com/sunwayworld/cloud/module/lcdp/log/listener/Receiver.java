/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.lcdp.log.listener;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONValidator;
import com.sunwayworld.cloud.module.lcdp.log.ScriptLogInfo;
import com.sunwayworld.cloud.module.lcdp.log.ScriptLogWebSocket;

public class Receiver {
    public void receiveMessage(String message) {
        ScriptLogInfo logInfo;
        ScriptLogWebSocket scriptLogWebSocket;
        if (JSONValidator.from(message).validate() && (scriptLogWebSocket = ScriptLogWebSocket.webSocketSet.get((logInfo = JSON.parseObject(message, ScriptLogInfo.class)).getUser())) != null) {
            logInfo.setUser(null);
            scriptLogWebSocket.sendMessage(JSON.toJSONString(logInfo));
        }
    }
}
