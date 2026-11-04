/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.framework.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value=HttpStatus.INTERNAL_SERVER_ERROR)
public class ProcessException
extends RuntimeException {
    private static final long serialVersionUID = -814387225457095244L;

    public ProcessException() {
    }

    public ProcessException(String msg) {
        super(msg);
    }

    public ProcessException(Throwable t) {
        super(t);
    }
}
