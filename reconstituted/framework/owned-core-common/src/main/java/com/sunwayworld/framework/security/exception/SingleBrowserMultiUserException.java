/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.framework.security.exception;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value=HttpStatus.BAD_REQUEST)
public class SingleBrowserMultiUserException
extends AuthenticationException {
    private static final long serialVersionUID = -3929483497662161672L;

    public SingleBrowserMultiUserException() {
        super("GIKAM.SECURITY.LOGIN.ALREADY_LOGIN");
    }
}
