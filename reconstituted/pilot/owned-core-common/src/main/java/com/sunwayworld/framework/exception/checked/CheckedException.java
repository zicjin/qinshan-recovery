/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.framework.exception.checked;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value=HttpStatus.EXPECTATION_FAILED)
public class CheckedException
extends RuntimeException {
    private static final long serialVersionUID = 8972335819760994212L;

    public CheckedException() {
    }

    public CheckedException(String msg) {
        super(msg);
    }

    public CheckedException(String msg, Throwable throwable) {
        super(msg, throwable);
    }

    public CheckedException(Throwable throwable) {
        super(throwable);
    }
}
