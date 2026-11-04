/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.framework.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value=HttpStatus.INTERNAL_SERVER_ERROR)
public class QueueException
extends RuntimeException {
    private static final long serialVersionUID = 3820187417600099920L;

    public QueueException(Throwable cause) {
        super(cause);
    }
}
