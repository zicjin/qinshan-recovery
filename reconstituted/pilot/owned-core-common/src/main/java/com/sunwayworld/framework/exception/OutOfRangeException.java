/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.framework.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value=HttpStatus.INTERNAL_SERVER_ERROR)
public class OutOfRangeException
extends RuntimeException {
    private static final long serialVersionUID = -74975982546209140L;

    public OutOfRangeException(String msg) {
        super(msg);
    }
}
