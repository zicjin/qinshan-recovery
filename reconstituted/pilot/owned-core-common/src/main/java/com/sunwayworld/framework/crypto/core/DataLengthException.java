/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.framework.crypto.core;

import com.sunwayworld.framework.crypto.core.RuntimeCryptoException;

public class DataLengthException
extends RuntimeCryptoException {
    private static final long serialVersionUID = -6389414041012204855L;

    public DataLengthException() {
    }

    public DataLengthException(String message) {
        super(message);
    }
}
