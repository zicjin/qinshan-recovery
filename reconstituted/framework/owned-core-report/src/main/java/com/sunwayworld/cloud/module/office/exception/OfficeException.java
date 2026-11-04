/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.office.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value=HttpStatus.INTERNAL_SERVER_ERROR)
public class OfficeException
extends RuntimeException {
    private static final long serialVersionUID = -7854792968237674098L;

    public OfficeException(Throwable cause) {
        super(cause);
    }

    public OfficeException(String msg) {
        super(msg);
    }

    public OfficeException(String msg, Throwable cause) {
        super(msg, cause);
    }
}
