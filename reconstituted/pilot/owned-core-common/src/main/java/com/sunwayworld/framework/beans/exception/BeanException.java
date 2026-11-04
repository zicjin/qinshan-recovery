/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.framework.beans.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value=HttpStatus.INTERNAL_SERVER_ERROR)
public class BeanException
extends RuntimeException {
    private static final long serialVersionUID = -6193426921421285936L;

    public BeanException(String message) {
        super(message);
    }
}
