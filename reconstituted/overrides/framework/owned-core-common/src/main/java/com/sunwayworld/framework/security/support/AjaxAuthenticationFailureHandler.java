/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.framework.security.support;

import com.sunwayworld.framework.exception.core.HttpErrorMessage;
import com.sunwayworld.framework.i18n.I18nHelper;
import com.sunwayworld.framework.security.exception.PreviousLoginDetectedException;
import com.sunwayworld.framework.utils.ServletUtils;
import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.stereotype.Component;

@Component
public class AjaxAuthenticationFailureHandler
extends SimpleUrlAuthenticationFailureHandler {
    private static final Logger logger = LogManager.getLogger(AjaxAuthenticationFailureHandler.class);

    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws IOException, ServletException {
        String message = exception.getMessage();
        logger.warn("Authentication failure: " + message, (Throwable) exception);
        if (PreviousLoginDetectedException.class.equals(exception.getClass())) {
            ServletUtils.responseError(HttpErrorMessage.of(HttpStatus.PAYMENT_REQUIRED, message));
        } else {
            ServletUtils.responseError(HttpErrorMessage.of(HttpStatus.UNAUTHORIZED, message));
        }
    }
}
