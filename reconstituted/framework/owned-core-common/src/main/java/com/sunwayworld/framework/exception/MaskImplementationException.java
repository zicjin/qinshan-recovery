/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.framework.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value=HttpStatus.INTERNAL_SERVER_ERROR)
public class MaskImplementationException
extends RuntimeException {
    private static final long serialVersionUID = 4274117768617586829L;

    public MaskImplementationException() {
    }

    public MaskImplementationException(String msg) {
        super(msg);
    }

    public MaskImplementationException(Throwable t) {
        super(t);
    }
}
