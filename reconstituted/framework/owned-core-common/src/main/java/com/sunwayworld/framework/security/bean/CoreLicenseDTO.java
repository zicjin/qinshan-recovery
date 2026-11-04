/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.framework.security.bean;

import java.io.Serializable;
import java.time.LocalDate;

public class CoreLicenseDTO
implements Serializable {
    private static final long serialVersionUID = -1766226025548649678L;
    private Long loginLimit;
    private LocalDate expiredDate;

    public Long getLoginLimit() {
        return this.loginLimit;
    }

    public void setLoginLimit(Long loginLimit) {
        this.loginLimit = loginLimit;
    }

    public LocalDate getExpiredDate() {
        return this.expiredDate;
    }

    public void setExpiredDate(LocalDate expiredDate) {
        this.expiredDate = expiredDate;
    }
}
