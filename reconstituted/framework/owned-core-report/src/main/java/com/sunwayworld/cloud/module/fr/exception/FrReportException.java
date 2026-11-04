/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.fr.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value=HttpStatus.INTERNAL_SERVER_ERROR)
public class FrReportException
extends RuntimeException {
    private static final long serialVersionUID = -7854792968237674098L;

    public FrReportException(Throwable cause) {
        super(cause);
    }

    public FrReportException(String msg) {
        super(msg);
    }
}
