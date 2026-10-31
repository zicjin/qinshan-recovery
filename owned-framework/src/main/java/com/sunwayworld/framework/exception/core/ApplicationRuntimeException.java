package com.sunwayworld.framework.exception.core;

import com.sunwayworld.owned.internal.i18n.OwnedMessageSource;

public class ApplicationRuntimeException extends RuntimeException {
    private static final long serialVersionUID = 1L;
    public ApplicationRuntimeException(String message, String... arguments) { super(OwnedMessageSource.getMessage(message, arguments)); }
    public ApplicationRuntimeException(Throwable cause) { super(cause); }
    public ApplicationRuntimeException(Throwable cause, String message, String... arguments) { super(OwnedMessageSource.getMessage(message, arguments), cause); }
}
