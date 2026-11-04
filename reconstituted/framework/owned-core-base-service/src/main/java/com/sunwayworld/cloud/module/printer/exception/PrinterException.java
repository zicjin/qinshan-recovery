/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.module.printer.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value=HttpStatus.INTERNAL_SERVER_ERROR)
public class PrinterException
extends RuntimeException {
    private static final long serialVersionUID = -7854792968237674098L;

    public PrinterException(Throwable cause) {
        super(cause);
    }

    public PrinterException(String msg) {
        super(msg);
    }

    public PrinterException(String msg, Throwable cause) {
        super(msg, cause);
    }
}
