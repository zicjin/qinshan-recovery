/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.mobile.dingtalk.utils;

public enum NotificationMsgType {
    TEXT("text", 0),
    IMAGE("image", 1),
    VOICE("voice", 2),
    FILE("file", 3),
    LINK("link", 4),
    OA("oa", 5),
    MARKDOWN("markdown", 6),
    ACTIONCARD("action_card", 7);

    private String type;
    private int code;

    private NotificationMsgType(String type, int code) {
        this.type = type;
        this.code = code;
    }

    public String getType() {
        return this.type;
    }

    public int getCode() {
        return this.code;
    }

    public static NotificationMsgType getNotificationMsgTypeByType(String type) {
        NotificationMsgType[] notificationMsgTypes;
        for (NotificationMsgType notificationMsgType : notificationMsgTypes = NotificationMsgType.values()) {
            if (!notificationMsgType.getType().equals(type)) continue;
            return notificationMsgType;
        }
        return TEXT;
    }

    public static NotificationMsgType getNotificationMsgTypeByCode(int code) {
        NotificationMsgType[] notificationMsgTypes;
        for (NotificationMsgType notificationMsgType : notificationMsgTypes = NotificationMsgType.values()) {
            if (notificationMsgType.getCode() != code) continue;
            return notificationMsgType;
        }
        return TEXT;
    }
}
