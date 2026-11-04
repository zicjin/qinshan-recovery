/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.framework.exception.checked;

import java.util.Objects;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value=HttpStatus.EXPECTATION_FAILED)
public class LoginUserExceededException
extends AuthenticationException {
    private static final long serialVersionUID = -6681873915879240967L;

    public LoginUserExceededException(String msg) {
        super(Objects.requireNonNull(msg, "GIKAM.SECURITY.LICENSE.LOGIN_USER_EXCEEDED"));
    }
}
