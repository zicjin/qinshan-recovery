/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.framework.i18n;

import com.sunwayworld.framework.utils.ArrayUtils;
import com.sunwayworld.framework.utils.StringUtils;
import com.sunwayworld.module.sys.i18n.CoreI18nCache;
import java.text.MessageFormat;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import org.springframework.context.ApplicationContext;
import org.springframework.context.MessageSource;
import org.springframework.context.MessageSourceResolvable;
import org.springframework.context.NoSuchMessageException;

public class DatabaseDrivenMessageSource
implements MessageSource {
    public DatabaseDrivenMessageSource(ApplicationContext applicationContext) {
    }

    @Override
    public String getMessage(String code, Object[] args, String defaultMessage, Locale locale) {
        return Optional.ofNullable(this.resolveMessage(code, args, locale)).orElse(defaultMessage);
    }

    @Override
    public String getMessage(String code, Object[] args, Locale locale) throws NoSuchMessageException {
        return this.resolveMessage(code, args, locale);
    }

    @Override
    public String getMessage(MessageSourceResolvable resolvable, Locale locale) throws NoSuchMessageException {
        for (String code : resolvable.getCodes()) {
            String message = this.resolveMessage(code, resolvable.getArguments(), locale);
            if (message == null) continue;
            return message;
        }
        return resolvable.getDefaultMessage();
    }

    private String resolveMessage(String code, Object[] args, Locale locale) {
        String message;
        StringBuilder sb = new StringBuilder(locale.getLanguage().toLowerCase());
        if (!StringUtils.isBlank(locale.getCountry())) {
            sb.append("-").append(locale.getCountry().toLowerCase());
        }
        String localeCode = sb.toString();
        Map<String, String> messageMap = CoreI18nCache.instance().getI18nLocaleMap(localeCode);
        if (messageMap != null && (message = messageMap.get(code)) != null) {
            if (ArrayUtils.isEmpty(args)) {
                return message;
            }
            return MessageFormat.format(message, args);
        }
        return code;
    }
}
