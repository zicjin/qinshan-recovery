/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.lims.reporttemplate.support.helper;

import com.sunwayworld.framework.exception.UnexpectedException;
import com.sunwayworld.framework.i18n.I18nHelper;
import com.sunwayworld.framework.utils.CollectionUtils;
import com.sunwayworld.framework.utils.StringUtils;
import java.util.List;
import java.util.Map;

public class LimsSqlHelper {
    public static final String prefix = "${";
    public static final String suffix = "}";

    public static String replaceParamString(String paramString, Map parameter) {
        return LimsSqlHelper.replaceParamString(paramString, parameter, null);
    }

    public static String replaceParamString(String paramString, Map parameter, Map defaultParam) {
        List<String> replaceList = StringUtils.substringsBetween(paramString, prefix, suffix);
        for (String paramKey : replaceList) {
            Object value = parameter.get(paramKey);
            if (value == null && !CollectionUtils.isEmpty(defaultParam)) {
                value = defaultParam.get(paramKey);
            }
            if (value == null) {
                throw new UnexpectedException(String.format(I18nHelper.getMessage("LIMS.MODULE.REPORTTEMPLATES.TIP.WITH_NO_PARAM", new String[0]), paramKey));
            }
            paramString = StringUtils.replace(paramString, prefix + paramKey + suffix, value);
        }
        return paramString;
    }

    public static List<String> getParamTagList(String target) {
        return StringUtils.substringsBetween(target, prefix, suffix);
    }
}
