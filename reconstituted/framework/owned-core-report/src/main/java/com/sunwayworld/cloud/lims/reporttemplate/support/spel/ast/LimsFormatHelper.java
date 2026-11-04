/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.cloud.lims.reporttemplate.support.spel.ast;

import java.util.List;
import java.util.StringJoiner;
import org.springframework.core.convert.TypeDescriptor;
import org.springframework.lang.Nullable;
import org.springframework.util.ClassUtils;

public class LimsFormatHelper {
    public static String formatMethodForMessage(String name, List<TypeDescriptor> argumentTypes) {
        StringJoiner sj = new StringJoiner(",", "(", ")");
        for (TypeDescriptor typeDescriptor : argumentTypes) {
            if (typeDescriptor != null) {
                sj.add(LimsFormatHelper.formatClassNameForMessage(typeDescriptor.getType()));
                continue;
            }
            sj.add(LimsFormatHelper.formatClassNameForMessage(null));
        }
        return name + sj.toString();
    }

    public static String formatClassNameForMessage(@Nullable Class<?> clazz) {
        return clazz != null ? ClassUtils.getQualifiedName(clazz) : "null";
    }
}
