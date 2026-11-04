/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.framework.i18n;

import com.sunwayworld.framework.utils.StringUtils;
import com.sunwayworld.module.sys.i18n.CoreI18nCache;
import java.util.Locale;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Component;

@Component
public class I18nHelper {
    private static Locale defaultLocale = Locale.CHINA;
    private static MessageSource messageSource;

    @Autowired
    public void setMessageSource(MessageSource messageSource) {
        I18nHelper.messageSource = messageSource;
    }

    public static String getMessage(String key, String ... args) {
        if (StringUtils.isBlank(key)) {
            return "";
        }
        Locale locale = LocaleContextHolder.getLocale();
        String message = messageSource.getMessage(key, args, locale);
        if ((StringUtils.isBlank(message) || key.equals(message)) && !defaultLocale.equals(locale)) {
            return messageSource.getMessage(key, args, defaultLocale);
        }
        return message;
    }

    public static String getMessageIfPresent(String key, String ... args) {
        if (StringUtils.isBlank(key)) {
            return null;
        }
        Locale locale = LocaleContextHolder.getLocale();
        String message = messageSource.getMessage(key, args, key, locale);
        if ((StringUtils.isBlank(message) || key.equals(message)) && !defaultLocale.equals(locale) && (StringUtils.isBlank(message = messageSource.getMessage(key, args, key, defaultLocale)) || key.equals(message))) {
            return null;
        }
        return message;
    }

    public static String getLang(Locale locale) {
        StringBuilder sb = new StringBuilder(locale.getLanguage().toLowerCase());
        if (!StringUtils.isEmpty(locale.getCountry())) {
            sb.append("-").append(locale.getCountry().toLowerCase());
        }
        return sb.toString();
    }

    public Map<String, String> i18nMap() {
        return CoreI18nCache.instance().getI18nLocaleMap(I18nHelper.getLang(LocaleContextHolder.getLocale()));
    }
}
