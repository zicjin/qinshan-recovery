/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.lcdp.resource.support;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONValidator;
import com.sunwayworld.cloud.module.lcdp.resource.support.LcdpMapperUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;

public class LcdpMapperSyncListener
implements MessageListener {
    private static final Logger logger = LoggerFactory.getLogger(LcdpMapperSyncListener.class);

    @Override
    public void onMessage(Message message, byte[] pattern) {
        String body = new String(message.getBody());
        if (JSONValidator.from(body).validate()) {
            LcdpMapperUtils.MapperInfo mapperInfo = JSON.parseObject(body, LcdpMapperUtils.MapperInfo.class);
            logger.info("lcdp-mapper loading:" + mapperInfo.getPath());
            LcdpMapperUtils.loadMapper(mapperInfo.getPath(), mapperInfo.getProFlag(), mapperInfo.getContent());
        }
    }
}
