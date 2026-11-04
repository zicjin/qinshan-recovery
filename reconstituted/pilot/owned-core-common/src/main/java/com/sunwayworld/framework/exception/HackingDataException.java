/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.framework.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value=HttpStatus.INTERNAL_SERVER_ERROR)
public class HackingDataException
extends RuntimeException {
    private static final long serialVersionUID = -1478017360360904746L;

    public HackingDataException(String message) {
        super(message);
    }
}
