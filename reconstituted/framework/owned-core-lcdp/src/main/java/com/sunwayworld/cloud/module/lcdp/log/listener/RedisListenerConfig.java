/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.lcdp.log.listener;

import com.sunwayworld.cloud.module.lcdp.log.listener.Receiver;
import com.sunwayworld.cloud.module.lcdp.resource.support.LcdpMapperSyncListener;
import com.sunwayworld.cloud.module.lcdp.resource.support.LcdpMapperUtils;
import com.sunwayworld.cloud.module.lcdp.resource.support.LcdpScriptSyncListener;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.listener.PatternTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.data.redis.listener.adapter.MessageListenerAdapter;

@Configuration
public class RedisListenerConfig {
    @Bean
    public RedisMessageListenerContainer container(RedisConnectionFactory connectionFactory, MessageListenerAdapter listenerAdapter) {
        RedisMessageListenerContainer container = new RedisMessageListenerContainer();
        container.setConnectionFactory(connectionFactory);
        container.addMessageListener((MessageListener)listenerAdapter, new PatternTopic("lcdp-log"));
        container.addMessageListener((MessageListener)new LcdpMapperSyncListener(), new PatternTopic(LcdpMapperUtils.LCDP_SYNC_MAPPER_TOPIC));
        container.addMessageListener((MessageListener)new LcdpScriptSyncListener(), new PatternTopic(LcdpMapperUtils.LCDP_SYNC_SCRIPT_TOPIC));
        return container;
    }

    @Bean
    public MessageListenerAdapter listenerAdapter() {
        return new MessageListenerAdapter(new Receiver(), "receiveMessage");
    }
}
