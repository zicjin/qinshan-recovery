/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.framework.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value=HttpStatus.INTERNAL_SERVER_ERROR)
public class UnsupportedException
extends RuntimeException {
    private static final long serialVersionUID = 6036721898128842632L;

    public UnsupportedException() {
    }

    public UnsupportedException(String msg) {
        super(msg);
    }

    public UnsupportedException(Throwable t) {
        super(t);
    }
}
