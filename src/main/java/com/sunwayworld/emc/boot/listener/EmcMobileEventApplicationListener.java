package com.sunwayworld.emc.boot.listener;

import org.springframework.stereotype.Component;

import com.sunwayworld.cloud.gikam.mobile.listener.MobileEventApplicationListener;
import com.sunwayworld.module.sys.bpmn.event.CoreBpmnNotificationEvent;

/**
 * 发送推送消息，需要各行业自己实现
 * 
 * @author magh@sunwayworld.com 2020年06月22日
 *
 */
@Component
public class EmcMobileEventApplicationListener extends MobileEventApplicationListener {

    @Override
    public void onApplicationEvent(CoreBpmnNotificationEvent event) {
    }
}
