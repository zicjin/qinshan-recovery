/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.framework.script.exeception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value=HttpStatus.CONTINUE)
public class BshIgnoreException
extends RuntimeException {
    private static final long serialVersionUID = -6762796249760976132L;

    public BshIgnoreException() {
    }

    public BshIgnoreException(String msg) {
        super(msg);
    }
}
