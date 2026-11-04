/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.framework.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value=HttpStatus.INTERNAL_SERVER_ERROR)
public class FormulaException
extends RuntimeException {
    private static final long serialVersionUID = 3199336072993488942L;

    public FormulaException(String message) {
        super(message);
    }
}
