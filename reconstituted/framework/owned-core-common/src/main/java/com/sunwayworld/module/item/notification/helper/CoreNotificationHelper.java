/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.module.item.notification.helper;

import com.sunwayworld.framework.context.ApplicationContextHelper;
import com.sunwayworld.framework.data.Pair;
import com.sunwayworld.framework.executor.ExecutorHelper;
import com.sunwayworld.framework.utils.ArrayUtils;
import com.sunwayworld.module.item.notification.bean.CoreNotificationDTO;
import com.sunwayworld.module.item.notification.service.CoreNotificationInstanceService;
import java.util.List;
import java.util.Map;

public class CoreNotificationHelper {
    public static void sendNotification(String templateId, List<String> notifiedUserIdList) {
        CoreNotificationHelper.sendNotification(templateId, notifiedUserIdList, null);
    }

    public static void sendNotification(String templateId, List<String> notifiedUserIdList, Map<String, Object> paramMap) {
        CoreNotificationHelper.sendNotification(templateId, notifiedUserIdList, paramMap, null);
    }

    public static void sendNotification(String templateId, List<String> notifiedUserIdList, Map<String, Object> paramMap, List<Pair<String, String>> filePairList) {
        CoreNotificationDTO notification = new CoreNotificationDTO();
        notification.setTemplateId(templateId);
        notification.setNotifiedIdList(notifiedUserIdList);
        notification.setParamMap(paramMap);
        notification.setFilePairList(filePairList);
        CoreNotificationHelper.sendNotificationList(ArrayUtils.asList(notification));
    }

    public static void sendNotificationList(List<CoreNotificationDTO> notificationList) {
        CoreNotificationInstanceService notificationInstanceService = ApplicationContextHelper.getBean(CoreNotificationInstanceService.class);
        ExecutorHelper.submit(() -> notificationInstanceService.insertNotificationList(notificationList));
    }
}
