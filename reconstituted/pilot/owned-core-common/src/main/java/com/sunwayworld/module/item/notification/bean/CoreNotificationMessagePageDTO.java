/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.module.item.notification.bean;

import com.sunwayworld.module.item.notification.bean.CoreNotificationMessageDTO;
import java.io.Serializable;
import java.util.List;

public class CoreNotificationMessagePageDTO
implements Serializable {
    private static final long serialVersionUID = -2070353527812397244L;
    private List<CoreNotificationMessageDTO> notificationMessageList;
    private int total;

    public List<CoreNotificationMessageDTO> getNotificationMessageList() {
        return this.notificationMessageList;
    }

    public void setNotificationMessageList(List<CoreNotificationMessageDTO> notificationMessageList) {
        this.notificationMessageList = notificationMessageList;
    }

    public int getTotal() {
        return this.total;
    }

    public void setTotal(int total) {
        this.total = total;
    }
}
