package com.sunwayworld.owned.internal.i18n;

import java.util.Locale;
import org.springframework.context.MessageSource;
import com.sunwayworld.owned.internal.context.OwnedApplicationContext;
import org.springframework.context.i18n.LocaleContextHolder;

/** Internal message lookup seam used by owned exceptions and converter leaves. */
public final class OwnedMessageSource {
    private static volatile MessageSource source;
    private OwnedMessageSource() {}
    public static void setMessageSource(MessageSource messageSource) { source = messageSource; }
    public static MessageSource getMessageSource() { return source; }
    public static String getMessage(String code, String... arguments) {
        if (isBlank(code)) return "";
        MessageSource messageSource = currentSource();
        if (messageSource == null) return code;
        Locale locale = LocaleContextHolder.getLocale();
        Object[] args = arguments == null ? null : arguments;
        String result = lookup(messageSource, code, args, locale, false);
        if (unresolved(code, result) && !Locale.CHINA.equals(locale)) result = lookup(messageSource, code, args, Locale.CHINA, false);
        return result;
    }
    public static String getMessageIfPresent(String code, String... arguments) {
        if (isBlank(code)) return null;
        MessageSource messageSource = currentSource();
        if (messageSource == null) return null;
        Locale locale = LocaleContextHolder.getLocale();
        Object[] args = arguments == null ? null : arguments;
        String result = lookup(messageSource, code, args, locale, true);
        if (unresolved(code, result) && !Locale.CHINA.equals(locale)) {
            result = lookup(messageSource, code, args, Locale.CHINA, true);
            if (unresolved(code, result)) return null;
        }
        return result;
    }
    private static String lookup(MessageSource messageSource, String code, Object[] args, Locale locale, boolean defaultMessage) {
        if (!defaultMessage) return messageSource.getMessage(code, args, locale);
        return messageSource.getMessage(code, args, code, locale);
    }
    private static MessageSource currentSource() {
        MessageSource configured = source;
        if (configured != null) return configured;
        return OwnedApplicationContext.getApplicationContext();
    }
    private static boolean unresolved(String code, String result) { return isBlank(result) || code.equals(result); }
    private static boolean isBlank(String value) {
        if (value == null || value.length() == 0) return true;
        for (int i = 0; i < value.length(); i++) if (!Character.isWhitespace(value.charAt(i))) return false;
        return true;
    }
}
