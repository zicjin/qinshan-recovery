/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.framework.security.exception;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value=HttpStatus.IM_USED)
public class PreviousLoginDetectedException
extends AuthenticationException {
    private static final long serialVersionUID = -6681873915879240967L;

    public PreviousLoginDetectedException(String previousLoginIp) {
        super(previousLoginIp);
    }
}
