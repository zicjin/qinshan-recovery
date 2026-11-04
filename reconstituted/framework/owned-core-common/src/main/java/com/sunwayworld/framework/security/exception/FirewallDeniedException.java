/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.framework.security.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value=HttpStatus.BAD_REQUEST)
public class FirewallDeniedException
extends RuntimeException {
    private static final long serialVersionUID = 7707586058087313379L;
}
