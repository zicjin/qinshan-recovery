/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.framework.validator.data;

import com.alibaba.fastjson.JSON;
import com.sunwayworld.framework.exception.core.HttpErrorMessage;
import com.sunwayworld.framework.i18n.I18nHelper;
import com.sunwayworld.framework.utils.ServletUtils;
import java.util.function.Predicate;
import javax.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;

public interface DataValidator {
    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    default public boolean isValid(Object ... args) {
        try {
            boolean bl = this.doValid(args);
            return bl;
        }
        finally {
            HttpServletRequest request = ServletUtils.getCurrentRequest();
            String constraintViolation = (String)request.getAttribute("validator.constraintViolation");
            if (constraintViolation != null) {
                ServletUtils.responseError(HttpErrorMessage.of(HttpStatus.BAD_REQUEST, constraintViolation));
            }
        }
    }

    public boolean doValid(Object ... var1);

    default public void addConstraintViolation(String message) {
        HttpServletRequest request = ServletUtils.getCurrentRequest();
        String constraintViolation = (String)request.getAttribute("validator.constraintViolation");
        if (constraintViolation == null) {
            constraintViolation = "";
        }
        constraintViolation = constraintViolation + message;
        request.setAttribute("validator.constraintViolation", constraintViolation);
    }

    default public <T> boolean test(String businessName, String table, String column, T item, Predicate<T> predicate, String message) {
        if (!predicate.test(item)) {
            this.addConstraintViolation(businessName + "\uff1a[" + I18nHelper.getMessage(table.toUpperCase() + "." + column.toUpperCase(), new String[0]) + "] " + I18nHelper.getMessage(message, new String[0]));
            return false;
        }
        return true;
    }

    default public boolean testNoneNull(String businessName, String table, String column, Object value) {
        if (value == null) {
            this.addConstraintViolation(businessName + "\uff1a[" + I18nHelper.getMessage(table.toUpperCase() + "." + column.toUpperCase(), new String[0]) + "] " + I18nHelper.getMessage("GIKAM.EXCEPTION.REQUIRE_NOT_EMPTY", new String[0]));
            return false;
        }
        return true;
    }

    default public boolean testJson(String businessName, String table, String column, String value) {
        try {
            JSON.parseObject(value);
            return true;
        }
        catch (Exception ex) {
            this.addConstraintViolation(businessName + "\uff1a[" + I18nHelper.getMessage(table.toUpperCase() + "." + column.toUpperCase(), new String[0]) + "] " + I18nHelper.getMessage("GIKAM.EXCEPTION.REQUIRE_JSON", new String[0]));
            return false;
        }
    }
}
