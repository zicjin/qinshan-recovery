/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.framework.exception.core;

import com.sunwayworld.framework.i18n.I18nHelper;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value=HttpStatus.BAD_REQUEST)
public class ApplicationWarningException
extends RuntimeException {
    private static final long serialVersionUID = -2021139321264652422L;

    public ApplicationWarningException(String i18nCode, String ... i18nParams) {
        super(I18nHelper.getMessage(i18nCode, i18nParams));
    }

    public ApplicationWarningException(Throwable cause) {
        super(cause);
    }

    public ApplicationWarningException(Throwable cause, String i18nCode, String ... i18nParams) {
        super(I18nHelper.getMessage(i18nCode, i18nParams), cause);
    }

    public ApplicationWarningException(String i18nCode, boolean maskStackTrace, String ... i18nParams) {
        super(I18nHelper.getMessage(i18nCode, i18nParams), null, !maskStackTrace, !maskStackTrace);
    }
}
