/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.framework.exception.advice;

import com.sunwayworld.framework.exception.CompileException;
import com.sunwayworld.framework.exception.checked.CheckedException;
import com.sunwayworld.framework.exception.core.ApplicationWarningException;
import com.sunwayworld.framework.exception.core.HttpErrorMessage;
import com.sunwayworld.framework.i18n.I18nHelper;
import com.sunwayworld.framework.log.annotation.Log;
import com.sunwayworld.framework.log.annotation.LogType;
import com.sunwayworld.framework.utils.ServletUtils;
import com.sunwayworld.framework.utils.StringUtils;
import io.sentry.Sentry;
import java.util.Optional;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

@ControllerAdvice
@RestControllerAdvice
@Order(value=-2147483648)
public class GlobalExceptionHandler {
    private static final Logger logger = LogManager.getLogger(GlobalExceptionHandler.class);
    @Value(value="${sunway.exception.viewable:true}")
    private boolean viewable;
    @Value(value="${sentry.enabled:false}")
    private boolean sentryEnable;

    @Deprecated
    @ExceptionHandler
    @ResponseStatus(value=HttpStatus.BAD_REQUEST)
    @Log(value="\u6570\u636e\u9a8c\u8bc1\u5f02\u5e38", type=LogType.EXCEPTION)
    public void handleCheckedException(CheckedException checkedException) {
        if (this.sentryEnable) {
            Sentry.captureException(checkedException);
        }
        ServletUtils.responseError(HttpErrorMessage.of(HttpStatus.BAD_REQUEST, I18nHelper.getMessage(checkedException.getMessage(), new String[0])));
    }

    @Deprecated
    @ExceptionHandler(value={CompileException.class})
    @ResponseStatus(value=HttpStatus.BAD_REQUEST)
    @Log(value="\u7f16\u8bd1\u5f02\u5e38", type=LogType.EXCEPTION)
    public void handleCompileException(CompileException compileException) {
        ServletUtils.responseError(HttpErrorMessage.of(HttpStatus.PRECONDITION_FAILED, compileException.getMessage()));
    }

    @ExceptionHandler(value={MethodArgumentTypeMismatchException.class})
    @ResponseStatus(value=HttpStatus.BAD_REQUEST)
    @Log(value="\u6570\u636e\u9a8c\u8bc1\u5f02\u5e38", type=LogType.EXCEPTION)
    public void handleMethodArgumentTypeMismatchException(MethodArgumentTypeMismatchException ex) {
        if (this.sentryEnable) {
            Sentry.captureException(ex);
        }
        ServletUtils.responseError(HttpErrorMessage.of(HttpStatus.BAD_REQUEST, "GIKAM.EXCEPTION.PARAM_ERROR"));
    }

    @ExceptionHandler
    @ResponseStatus(value=HttpStatus.INTERNAL_SERVER_ERROR)
    @Log(value="\u5185\u90e8\u5f02\u5e38", type=LogType.EXCEPTION)
    public void catchException(Throwable throwable) {
        Class<?> clazz;
        if (this.sentryEnable) {
            Sentry.captureException(throwable);
        }
        if (AuthenticationException.class.isAssignableFrom(clazz = (throwable = this.getNestedThrowable(throwable)).getClass())) {
            String message = StringUtils.isBlank(throwable.getMessage()) ? I18nHelper.getMessage("GIKAM.EXCEPTION.UNEXPECTED", new String[0]) : I18nHelper.getMessage(throwable.getMessage(), new String[0]);
            ServletUtils.responseError(HttpErrorMessage.of(HttpStatus.BAD_REQUEST, message));
            return;
        }
        ResponseStatus status = clazz.getAnnotation(ResponseStatus.class);
        if (throwable.getStackTrace().length > 0) {
            logger.error(throwable.getMessage(), throwable);
        }
        if (status == null || status.value().equals((Object)HttpStatus.INTERNAL_SERVER_ERROR)) {
            String message;
            String string = message = AuthenticationException.class.isAssignableFrom(clazz) || throwable.getCause() == null && StringUtils.startsWith(throwable.getClass().getName(), "com.sunwayworld") ? I18nHelper.getMessage(throwable.getMessage(), new String[0]) : I18nHelper.getMessage("GIKAM.EXCEPTION.UNEXPECTED", new String[0]);
            if (this.viewable) {
                ServletUtils.responseError(HttpErrorMessage.of(HttpStatus.INTERNAL_SERVER_ERROR, message, throwable));
            } else {
                ServletUtils.responseError(HttpErrorMessage.of(HttpStatus.INTERNAL_SERVER_ERROR, message));
            }
        } else {
            int statusCode = status.value().value();
            if (HttpStatus.NOT_FOUND.value() == statusCode) {
                statusCode = HttpStatus.INTERNAL_SERVER_ERROR.value();
            }
            ServletUtils.responseError(HttpErrorMessage.of(HttpStatus.valueOf(statusCode), I18nHelper.getMessage(throwable.getMessage(), new String[0])));
        }
    }

    @ExceptionHandler
    @ResponseStatus(value=HttpStatus.BAD_REQUEST)
    @Log(value="\u6570\u636e\u9a8c\u8bc1\u5f02\u5e38", type=LogType.EXCEPTION)
    public void warningException(ApplicationWarningException warningException) {
        String message;
        if (this.sentryEnable) {
            Sentry.captureException(warningException);
        }
        if (StringUtils.isBlank(message = warningException.getMessage())) {
            logger.warn(warningException);
        } else {
            logger.warn(I18nHelper.getMessageIfPresent(message, new String[0]), (Throwable)warningException);
        }
        ServletUtils.responseError(HttpErrorMessage.of(HttpStatus.BAD_REQUEST, message));
    }

    private Throwable getNestedThrowable(Throwable throwable) {
        Throwable clone;
        for (clone = throwable; clone != null && !StringUtils.startsWith(clone.getClass().getName(), "com.sunwayworld.") && !AuthenticationException.class.isAssignableFrom(clone.getClass()); clone = clone.getCause()) {
        }
        return Optional.ofNullable(clone).orElse(throwable);
    }
}
