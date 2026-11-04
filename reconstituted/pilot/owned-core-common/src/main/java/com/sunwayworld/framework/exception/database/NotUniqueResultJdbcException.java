/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.framework.exception.database;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value=HttpStatus.INTERNAL_SERVER_ERROR)
public class NotUniqueResultJdbcException
extends RuntimeException {
    private static final long serialVersionUID = 6682538888091614828L;
}
