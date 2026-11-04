/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.framework.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value=HttpStatus.INTERNAL_SERVER_ERROR)
public class MppException
extends RuntimeException {
    private static final long serialVersionUID = -8945764214538663859L;

    public MppException(Throwable cause) {
        super(cause);
    }

    public MppException(String msg) {
        super(msg);
    }
}
