/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.module.sys.remotecall.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value=HttpStatus.INTERNAL_SERVER_ERROR)
public class CoreRemoteCallException
extends RuntimeException {
    private static final long serialVersionUID = -7111276261377629202L;

    public CoreRemoteCallException(Exception ex) {
        super(ex);
    }
}
