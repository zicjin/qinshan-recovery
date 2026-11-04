/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.lcdp.base.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.sunwayworld.cloud.module.lcdp.log.ScriptLogWebSocket;
import com.sunwayworld.framework.context.ApplicationContextHelper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public abstract class LcdpBaseUtils {
    Logger logger = LogManager.getLogger(LcdpBaseUtils.class);

    public void log(Object log) {
        String message = "";
        Object resultJsonObject = JSON.parse(JSONObject.toJSONStringWithDateFormat(log, "yyyy-MM-dd HH:mm:ss", new SerializerFeature[0]));
        message = resultJsonObject instanceof JSONObject ? JSONObject.toJSONString(resultJsonObject, true) : (resultJsonObject instanceof JSONArray ? JSONArray.toJSONString(resultJsonObject, true) : String.valueOf(log));
        ScriptLogWebSocket console = ApplicationContextHelper.getBean(ScriptLogWebSocket.class);
        console.pushInfoLog(message);
        this.logger.info(log);
    }
}
