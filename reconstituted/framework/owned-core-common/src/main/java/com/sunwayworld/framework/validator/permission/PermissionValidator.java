/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.framework.validator.permission;

import com.sunwayworld.framework.exception.core.HttpErrorMessage;
import com.sunwayworld.framework.utils.ServletUtils;
import javax.servlet.http.HttpServletRequest;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.HttpStatus;

public interface PermissionValidator {
    public static final Logger logger = LogManager.getLogger(PermissionValidator.class);

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    default public boolean isValid(String table, String category, Object id) {
        try {
            boolean bl = this.doValid(table, category, id);
            return bl;
        }
        finally {
            HttpServletRequest request = ServletUtils.getCurrentRequest();
            String constraintViolation = (String)request.getAttribute("validator.unauthorized");
            if (constraintViolation != null) {
                ServletUtils.responseError(HttpErrorMessage.of(HttpStatus.BAD_REQUEST, constraintViolation));
            }
        }
    }

    public boolean doValid(String var1, String var2, Object var3);

    default public void addConstraintViolation(String message) {
        HttpServletRequest request = ServletUtils.getCurrentRequest();
        String constraintViolation = (String)request.getAttribute("validator.unauthorized");
        constraintViolation = constraintViolation == null ? "" : constraintViolation + "; ";
        constraintViolation = constraintViolation + message;
        request.setAttribute("validator.unauthorized", constraintViolation);
    }
}
