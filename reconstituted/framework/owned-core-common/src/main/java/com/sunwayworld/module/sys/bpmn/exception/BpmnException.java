/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.module.sys.bpmn.exception;

import com.sunwayworld.framework.i18n.I18nHelper;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value=HttpStatus.INTERNAL_SERVER_ERROR)
public class BpmnException
extends RuntimeException {
    private static final long serialVersionUID = 3199336072993488942L;

    public BpmnException(String i18nCode, String ... i18nParams) {
        super(I18nHelper.getMessage(i18nCode, i18nParams));
    }
}
