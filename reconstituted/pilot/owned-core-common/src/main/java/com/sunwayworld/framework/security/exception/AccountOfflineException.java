/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.framework.security.exception;

import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AccountStatusException;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value=HttpStatus.GONE)
public class AccountOfflineException
extends AccountStatusException {
    private static final long serialVersionUID = -1502638744224739079L;

    public AccountOfflineException(String msg) {
        super(msg);
    }
}
