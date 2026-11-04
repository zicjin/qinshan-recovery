/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.module.sys.menu.log.bean;

import java.util.Optional;

public class CoreMenuAccessLogDTO {
    private String userId;
    private String menuId;
    private Long accessTimes;

    public static CoreMenuAccessLogDTO of(String userId, String menuId) {
        CoreMenuAccessLogDTO instance = new CoreMenuAccessLogDTO();
        instance.userId = userId;
        instance.menuId = menuId;
        instance.accessTimes = 1L;
        return instance;
    }

    public String getUserId() {
        return this.userId;
    }

    public String getMenuId() {
        return this.menuId;
    }

    public Long getAccessTimes() {
        return this.accessTimes;
    }

    public void increaseAccessTimes() {
        CoreMenuAccessLogDTO coreMenuAccessLogDTO = this;
        Long l = coreMenuAccessLogDTO.accessTimes;
        Long l2 = coreMenuAccessLogDTO.accessTimes = Long.valueOf(coreMenuAccessLogDTO.accessTimes + 1L);
    }

    public boolean equals(Object target) {
        if (target == null) {
            return false;
        }
        if (!CoreMenuAccessLogDTO.class.equals(target.getClass())) {
            return false;
        }
        CoreMenuAccessLogDTO targetLog = (CoreMenuAccessLogDTO)target;
        return targetLog.getUserId().equals(this.getUserId()) && targetLog.getMenuId().equals(this.getMenuId());
    }

    public int hashCode() {
        return (Optional.ofNullable(this.getUserId()).orElse("") + "-" + Optional.ofNullable(this.getMenuId()).orElse("")).hashCode();
    }
}
