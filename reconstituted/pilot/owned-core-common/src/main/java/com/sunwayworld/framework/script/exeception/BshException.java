/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.framework.script.exeception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value=HttpStatus.INTERNAL_SERVER_ERROR)
public class BshException
extends RuntimeException {
    private static final long serialVersionUID = -6762796249760976132L;

    public BshException(Throwable cause) {
        super(cause);
    }

    public BshException(String msg) {
        super(msg);
    }

    public BshException(String msg, Throwable cause) {
        super(msg, cause);
    }
}
