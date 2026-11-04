/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.framework.exception.database;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value=HttpStatus.INTERNAL_SERVER_ERROR)
public class MisMatchColumnNumberJdbcException
extends RuntimeException {
    private static final long serialVersionUID = 2498308275412293679L;

    public MisMatchColumnNumberJdbcException(int expectedColumnCount, int actualColumnCount) {
        super("Expected column number [" + expectedColumnCount + "], actual [" + actualColumnCount + "].");
    }
}
